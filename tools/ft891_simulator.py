#!/usr/bin/env python3
"""
Yaesu FT-891 CAT Serial / Network Simulator
Simulates a Yaesu FT-891 HF/50MHz transceiver responding to standard CAT commands over TCP or Serial.
Used for automated testing, CI, and local development with MiauDX / CatController.

Supported Yaesu FT-891 CAT Commands:
  - FA<10_digits>;  : Set VFO-A Frequency (e.g. FA0014074000; or FA00014074000;)
  - FA;             : Query VFO-A Frequency -> returns FA00014074000;
  - FB<10_digits>;  : Set VFO-B Frequency
  - FB;             : Query VFO-B Frequency -> returns FB00007074000;
  - MD0<n>;         : Set Operating Mode (1=LSB/USB/SSB, 2=CW, 3=CW-R, 4=FM, 5=AM)
  - MD0;            : Query Operating Mode -> returns MD01;
  - SM0;            : Query Signal S-Meter -> returns SM0009; (S9)
  - TX; / TX1;      : PTT Transmit On
  - RX; / TX0;      : PTT Transmit Off (Receive)
  - IF;             : Information query (frequency, mode, shift)
"""

import sys
import socket
import threading
import time

class FT891Simulator:
    def __init__(self, host="127.0.0.1", port=8910):
        self.host = host
        self.port = port
        self.vfo_a_hz = 14074000  # 14.074 MHz (20m FT8)
        self.vfo_b_hz = 7074000   # 7.074 MHz (40m FT8)
        self.mode = "1"           # 1: USB/SSB
        self.s_meter = 9          # S9
        self.tx = False
        self.running = False
        self.lock = threading.Lock()
        self.command_log = []

    def handle_command(self, cmd_str):
        """Processes a single Yaesu CAT command ending in ';'."""
        cmd = cmd_str.strip()
        if not cmd:
            return None

        with self.lock:
            self.command_log.append(cmd)
            # Keep last 50 logs
            if len(self.command_log) > 50:
                self.command_log.pop(0)

            # Query VFO-A
            if cmd == "FA;":
                resp = f"FA{self.vfo_a_hz:011d};"
                print(f"[FT891] Query VFO-A -> {resp}")
                return resp

            # Set VFO-A: e.g. FA00014074000; or FA0014074000;
            if cmd.startswith("FA") and cmd.endswith(";"):
                freq_digits = cmd[2:-1]
                if freq_digits.isdigit():
                    self.vfo_a_hz = int(freq_digits)
                    print(f"[FT891] Set VFO-A -> {self.vfo_a_hz:,} Hz ({self.vfo_a_hz / 1e6:.3f} MHz)")
                return f"FA{self.vfo_a_hz:011d};"

            # Query VFO-B
            if cmd == "FB;":
                resp = f"FB{self.vfo_b_hz:011d};"
                print(f"[FT891] Query VFO-B -> {resp}")
                return resp

            # Set VFO-B
            if cmd.startswith("FB") and cmd.endswith(";"):
                freq_digits = cmd[2:-1]
                if freq_digits.isdigit():
                    self.vfo_b_hz = int(freq_digits)
                    print(f"[FT891] Set VFO-B -> {self.vfo_b_hz:,} Hz")
                return f"FB{self.vfo_b_hz:011d};"

            # Query Mode
            if cmd == "MD0;" or cmd == "MD;":
                resp = f"MD0{self.mode};"
                print(f"[FT891] Query Mode -> {resp}")
                return resp

            # Set Mode: e.g. MD01; (SSB/USB), MD04; (FM)
            if cmd.startswith("MD") and cmd.endswith(";"):
                m_code = cmd[2:-1]
                if m_code.startswith("0"):
                    m_code = m_code[1:]
                self.mode = m_code
                mode_names = {"1": "SSB (USB/LSB)", "2": "CW", "4": "FM", "5": "AM"}
                print(f"[FT891] Set Mode -> {mode_names.get(self.mode, self.mode)}")
                return f"MD0{self.mode};"

            # S-Meter Query
            if cmd.startswith("SM"):
                resp = f"SM0{self.s_meter:03d};"
                print(f"[FT891] Query S-Meter -> {resp}")
                return resp

            # PTT Commands
            if cmd in ("TX;", "TX1;"):
                self.tx = True
                print("[FT891] PTT ON (Transmitting)")
                return None
            if cmd in ("RX;", "TX0;"):
                self.tx = False
                print("[FT891] PTT OFF (Receiving)")
                return None

            # Fallback ACK for unhandled commands
            print(f"[FT891] Received command: {cmd}")
            return cmd

    def run_server(self):
        """Starts TCP server simulating CAT port."""
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server.bind((self.host, self.port))
        server.listen(5)
        self.running = True

        print("=====================================================")
        print("          Yaesu FT-891 CAT Transceiver Simulator     ")
        print("=====================================================")
        print(f"Listening on TCP: {self.host}:{self.port}")
        print(f"Initial VFO-A:    {self.vfo_a_hz / 1e6:.3f} MHz (USB)")
        print(f"Initial S-Meter:  S{self.s_meter}")
        print("Ready for MiauDX / CAT controller connections.")
        print("=====================================================")

        try:
            while self.running:
                client, addr = server.accept()
                print(f"[FT891] Client connected: {addr}")
                t = threading.Thread(target=self.handle_client, args=(client,), daemon=True)
                t.start()
        except KeyboardInterrupt:
            print("\n[FT891] Simulator stopped by user.")
        finally:
            server.close()

    def handle_client(self, client):
        buffer = ""
        try:
            while self.running:
                data = client.recv(1024)
                if not data:
                    break
                buffer += data.decode("utf-8", errors="ignore")
                while ";" in buffer:
                    idx = buffer.index(";")
                    cmd = buffer[:idx+1]
                    buffer = buffer[idx+1:]
                    resp = self.handle_command(cmd)
                    if resp:
                        client.sendall(resp.encode("utf-8"))
        except Exception as e:
            print(f"[FT891] Client connection error: {e}")
        finally:
            client.close()
            print("[FT891] Client disconnected.")

if __name__ == "__main__":
    if "--test" in sys.argv:
        # Self-test mode
        sim = FT891Simulator()
        print("Running FT-891 Simulator self-test...")
        assert sim.handle_command("FA00014074000;") == "FA00014074000;"
        assert sim.vfo_a_hz == 14074000
        assert sim.handle_command("FA;") == "FA00014074000;"
        assert sim.handle_command("MD01;") == "MD01;"
        assert sim.handle_command("MD0;") == "MD01;"
        assert sim.handle_command("SM0;") == "SM0009;"
        print("[OK] All FT-891 CAT simulator self-tests passed!")
    else:
        port = int(sys.argv[1]) if len(sys.argv) > 1 and sys.argv[1].isdigit() else 8910
        sim = FT891Simulator(port=port)
        sim.run_server()
