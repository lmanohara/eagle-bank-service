package com.eaglebank.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${jwt.private-key}")
  private Resource privateKeyResource;

  @Value("${jwt.public-key}")
  private Resource publicKeyResource;

  @Value("${jwt.expiration}")
  private long expiration;

  private PrivateKey privateKey;
  private PublicKey publicKey;

  @PostConstruct
  void init() throws Exception {
    this.privateKey = loadPrivateKey(privateKeyResource);
    this.publicKey = loadPublicKey(publicKeyResource);
  }

  public String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(privateKey, SignatureAlgorithm.RS256)
        .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(publicKey)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validate(String token) {
    try {
      extractUsername(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private static PrivateKey loadPrivateKey(Resource resource) throws Exception {
    byte[] decoded = decodePem(resource, "PRIVATE KEY");
    return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
  }

  private static PublicKey loadPublicKey(Resource resource) throws Exception {
    byte[] decoded = decodePem(resource, "PUBLIC KEY");
    return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
  }

  private static byte[] decodePem(Resource resource, String label) throws Exception {
    String pem =
        new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
            .replace("-----BEGIN " + label + "-----", "")
            .replace("-----END " + label + "-----", "")
            .replaceAll("\\s+", "");
    return Base64.getDecoder().decode(pem);
  }
}
