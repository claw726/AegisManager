package com.aegis.project.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class TwoFactorAuthService {
    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

    public String generateSecretKey() {
        return googleAuthenticator.createCredentials().getKey();
    }

    public boolean verifyCode(String secretKey, int code) {
        return googleAuthenticator.authorize(secretKey, code);
    }

    public String getQRBarcodeURL(String user, String secret) {
        String serviceName = "Aegis";
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", serviceName, user, secret, serviceName);
    }

    public byte[] generateQRCodeImage(String barcodeURL) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(barcodeURL, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
