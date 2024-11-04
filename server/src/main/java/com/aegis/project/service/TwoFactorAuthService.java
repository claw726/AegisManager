package com.aegis.project.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorAuthService {

  private final GoogleAuthenticator googleAuthenticator =
    new GoogleAuthenticator();

  public String generateSecretKey() {
    return googleAuthenticator.createCredentials().getKey();
  }

  public boolean verifyCode(String secretKey, int code) {
    return googleAuthenticator.authorize(secretKey, code);
  }

  public String getQRBarcodeURL(int userID, String secret) {
    String serviceName = "Aegis";
    return String.format(
      "otpauth://totp/%s:%d?secret=%s&issuer=%s",
      serviceName,
      userID,
      secret,
      serviceName
    );
  }

  public byte[] generateQRCodeImage(String barcodeURL)
    throws WriterException, IOException {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(
      barcodeURL,
      BarcodeFormat.QR_CODE,
      200,
      200
    );
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
}
