package ifs.meuonibus.Services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

   @Autowired
   private JavaMailSender mailSender;
   @Value("${spring.mail.username}")
   private String remetente;

    public String enviarEmail(String destinatario, String assunto, String texto) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(remetente);
            message.setTo(destinatario);
            message.setSubject(assunto);
            message.setText(texto);
            mailSender.send(message);

            return "Email Enviado";




        } catch (Exception e) {

            return "Erro ao enviar e-mail"+e.getMessage();

        }

    }
}
