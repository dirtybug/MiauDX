#!/usr/bin/env python3
"""
Yaesu FT-857 / FT-857D CAT Transceiver Simulator
Simulates a Yaesu FT-857D HF/VHF/UHF transceiver responding to 5-byte binary
and 10-character hex CAT commands over TCP or serial loopback.
Used for automated testing, CI, and development with MiauDX / CatController.

Supported Yaesu FT-857 / FT-857D CAT Opcodes (Byte 5):
  - 0x01: Set Frequency (Bytes 1-4: 4-byte BCD frequency in 10Hz units)
  - 0x07: Set Mode (Byte 1: 00=LSB, 01=USB, 02=CW, 03=CWR, 04=AM, 05=FM, 06=DIG, 07=PKT, 08=FMN)
  - 0x08: PTT ON (00 00 00 00 08)
  - 0x88: PTT OFF (00 00 00 00 88)
  - 0x03: Read Frequency & Mode Status -> Returns 5 bytes: [4 bytes BCD Freq][1 byte Mode]
  - 0xE7: Read Receiver Status / S-Meter -> Returns 1 byte: S-Meter value
"""

import sys
import socket
import threading
import time

class FT857Simulator:
    def __init__(self, host="127.0.0.1", port=8570):
        self.host = host
        self.port = port
        self.freq_hz = 14074000  # 14.074 MHz (20m FT8)
        self.mode_byte = 0x01     # 0x01: USB
        self.s_meter = 9          # S9
        self.tx = False
        self.running = False
        self.lock = threading.Lock()
        self.command_log = []

    def bcd_to_freq(self, bcd_bytes):
        """Converts 4 BCD bytes to frequency in Hz."""
        freq = 0
        for b in bcd_bytes:
            high = (b >> 4) & 0x0F
            low = b & 0x0F
            freq = freq * 100 + (high * 10 + low)
        return freq * 10  # 10 Hz units

    def freq_to_bcd(self, freq_hz):
        """Converts frequency in Hz to 4 BCD bytes."""
        val = freq_hz // 10
        bcd = bytearray(4)
        for i in range(3, -1, -1):
            low = val % 10
            val //= 10
            high = val % 10
            val //= 10
            bcd[i] = (high << 4) | low
        return bytes(bcd)

    def handle_raw_command(self, data):
        """Handles 5-byte binary or 10-hex-character CAT command."""
        if isinstance(data, str):
            data = bytes.fromhex(data.strip())

        if len(data) != 5:
            return None

        opcode = data[4]
        with self.lock:
            self.command_log.append(data.hex())

            # 0x01: Set Frequency
            if opcode == 0x01:
                self.freq_hz = self.bcd_to_freq(data[:4])
                print(f"[FT857] Set Frequency -> {self.freq_hz:,} Hz ({self.freq_hz / 1e6:.3f} MHz)")
                return bytes([0x00])

            # 0x07: Set Mode
            if opcode == 0x07:
                self.mode_byte = data[0]
                mode_names = {
                    0x00: "LSB", 0x01: "USB", 0x02: "CW", 0x03: "CWR",
                    0x04: "AM", 0x05: "FM", 0x06: "DIG", 0x07: "PKT", 0x08: "FMN"
                }
                print(f"[FT857] Set Mode -> {mode_names.get(self.mode_byte, f'0x{self.mode_byte:02X}')}")
                return bytes([0x00])

            # 0x08: PTT ON
            if opcode == 0x08:
                self.tx = True
                print("[FT857] PTT ON (Transmitting)")
                return bytes([0x00])

            # 0x88: PTT OFF
            if opcode == 0x88:
                self.tx = False
                print("[FT857] PTT OFF (Receiving)")
                return bytes([0x00])

            # 0x03: Read Frequency and Mode
            if opcode == 0x03:
                resp = bytearray(self.freq_to_bcd(self.freq_hz))
                resp.append(self.mode_byte)
                print(f"[FT857] Read Status -> {self.freq_hz:,} Hz, Mode: 0x{self.mode_byte:02X}")
                return bytes(resp)

            # 0xE7: Read S-Meter
            if opcode == 0xE7:
                sm_byte = min(15, self.s_meter)
                print(f"[FT857] Read S-Meter -> S{self.s_meter}")
                return bytes([sm_byte])

            print(f"[FT857] Unknown Opcode: 0x{opcode:02X}")
            return bytes([0x00])

    def run_server(self):
        """Starts TCP server simulating FT-857 CAT interface."""
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server.bind((self.host, self.port))
        server.listen(5)
        self.running = True

        print("=====================================================")
        print("         Yaesu FT-857 / FT-857D CAT Simulator        ")
        print("=====================================================")
        print(f"Listening on TCP: {self.host}:{self.port}")
        print(f"Initial Frequency:{self.freq_hz / 1e6:.3f} MHz (USB)")
        print(f"Initial S-Meter:  S{self.s_meter}")
        print("Ready for MiauDX / CatController CAT connections.")
        print("=====================================================")

        try:
            while self.running:
                client, addr = server.accept()
                print(f"[FT857] Client connected: {addr}")
                t = threading.Thread(target=self.handle_client, args=(client,), daemon=True)
                t.start()
        except KeyboardInterrupt:
            print("\n[FT857] Simulator stopped.")
        finally:
            server.close()

    def handle_client(self, client):
        buffer = bytearray()
        try:
            while self.running:
                chunk = client.recv(1024)
                if not chunk:
                    break
                buffer.extend(chunk)
                while len(buffer) >= 5:
                    cmd = buffer[:5]
                    buffer = buffer[5:]
                    resp = self.handle_raw_command(cmd)
                    if resp:
                        client.sendall(resp)
        except Exception as e:
            print(f"[FT857] Client error: {e}")
        finally:
            client.close()
            print("[FT857] Client disconnected.")

if __name__ == "__main__":
    if "--test" in sys.argv:
        sim = FT857Simulator()
        print("Running FT-857 Simulator self-test...")
        # 14.074.000 Hz = 01 40 74 00 (BCD in 10Hz units: 1407400 = 01 40 74 00) + 0x01
        resp = sim.handle_raw_command(bytes([0x01, 0x40, 0x74, 0x00, 0x01]))
        assert resp == bytes([0x00])
        assert sim.freq_hz == 14074000

        # Mode USB (0x01) + 0x07
        resp = sim.handle_raw_command(bytes([0x01, 0x00, 0x00, 0x00, 0x07]))
        assert resp == bytes([0x00])
        assert sim.mode_byte == 0x01

        # Read Status
        status = sim.handle_raw_command(bytes([0x00, 0x00, 0x00, 0x00, 0x03]))
        assert len(status) == 5
        assert status[4] == 0x01  # USB

        # Read S-Meter
        sm = sim.handle_raw_command(bytes([0x00, 0x00, 0x00, 0x00, 0xE7]))
        assert len(sm) == 1
        assert sm[0] == 9

        print("[OK] All FT-857 CAT simulator self-tests passed!")
    else:
        port = int(sys.argv[1]) if len(sys.argv) > 1 and sys.argv[1].isdigit() else 8570
        sim = FT857Simulator(port=port)
        sim.run_server()
