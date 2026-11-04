package com.Runner.CQMiau;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AboutDialogFragment extends DialogFragment {

    public static final String RADIO_ESCOLA_URL = "https://radioescola.pt";
    public static final String RADIO_ESCOLA_PAYPAL_URL = "https://www.paypal.me/radioescola";
    public static final String RADIO_ESCOLA_DONATIVOS_URL = "https://radioescola.pt/donativos";
    public static final String REPO_URL = "https://github.com/dirtybug/MiauDX/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_about, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvVersion = view.findViewById(R.id.tvAboutVersion);
        if (tvVersion != null) {
            tvVersion.setText("v" + BuildConfig.VERSION_NAME);
        }

        View.OnClickListener openPayPal = v -> openUrl(RADIO_ESCOLA_PAYPAL_URL);
        View.OnClickListener openRadioEscola = v -> openUrl(RADIO_ESCOLA_URL);
        View.OnClickListener openGitHub = v -> openUrl(REPO_URL);

        Button btnDonatePayPal = view.findViewById(R.id.btnDonatePayPal);
        if (btnDonatePayPal != null) {
            btnDonatePayPal.setOnClickListener(openPayPal);
        }

        Button btnRadioEscolaWebsite = view.findViewById(R.id.btnRadioEscolaWebsite);
        if (btnRadioEscolaWebsite != null) {
            btnRadioEscolaWebsite.setOnClickListener(openRadioEscola);
        }

        ImageView ivRadioEscolaIcon = view.findViewById(R.id.ivRadioEscolaIcon);
        if (ivRadioEscolaIcon != null) {
            ivRadioEscolaIcon.setOnClickListener(openRadioEscola);
        }

        TextView tvPoweredBy = view.findViewById(R.id.tvPoweredBy);
        if (tvPoweredBy != null) {
            tvPoweredBy.setOnClickListener(openRadioEscola);
        }

        TextView tvRadioEscolaUrl = view.findViewById(R.id.tvRadioEscolaUrl);
        if (tvRadioEscolaUrl != null) {
            tvRadioEscolaUrl.setOnClickListener(openRadioEscola);
        }

        Button btnGitHub = view.findViewById(R.id.btnGitHub);
        if (btnGitHub != null) {
            btnGitHub.setOnClickListener(openGitHub);
        }

        TextView tvRepoUrl = view.findViewById(R.id.tvRepoUrl);
        if (tvRepoUrl != null) {
            tvRepoUrl.setOnClickListener(openGitHub);
        }

        Button btnClose = view.findViewById(R.id.btnCloseAbout);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dismiss());
        }
    }

    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception ignored) {
        }
    }
}
