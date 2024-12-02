package br.com.grupoirrah.euphoriabot.core.usecase.image;

import br.com.grupoirrah.euphoriabot.core.gateway.image.GenerateImageGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateImageUseCase implements GenerateImageGateway {

    @Override
    public void welcomeImage(String username, String avatarUrl) {
        try {
            BufferedImage background = ImageIO.read(new File("src/main/resources/image/backGround.png"));
            BufferedImage avatar = ImageIO.read(new URL(avatarUrl));

            BufferedImage resizedAvatar = resizeImage(avatar);
            BufferedImage maskedAvatar = applyCircularCrop(resizedAvatar);
            Graphics2D graphics = background.createGraphics();

            graphics.drawImage(maskedAvatar, 20, 30, null);

            graphics.setFont(new Font("SansSerif", Font.BOLD, 32));
            graphics.setColor(Color.BLACK);
            graphics.drawString(username, 150, 230);

            graphics.dispose();

            File output = new File("src/main/resources/image/welcome.png");
            ImageIO.write(background, "png", output);

            log.info("Imagem de boas-vindas gerada com sucesso!");

        } catch (IOException e) {
            log.error("Erro ao processar a imagem: {}", e.getMessage());
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage) {
        Image tmp = originalImage.getScaledInstance(160, 160, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(160, 160, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resized;
    }

    private BufferedImage applyCircularCrop(BufferedImage source) {
        BufferedImage circularImage = new BufferedImage(160, 160, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = circularImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, 160, 160));
        g2d.drawImage(source, 0, 0, 160, 160, null);
        g2d.dispose();

        return circularImage;
    }

}