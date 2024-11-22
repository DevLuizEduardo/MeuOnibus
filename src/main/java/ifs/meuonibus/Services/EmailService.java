package ifs.meuonibus.Services;


import jakarta.mail.internet.MimeMessage;
import jakarta.validation.MessageInterpolator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class EmailService {

   @Autowired
   private JavaMailSender mailSender;
   @Autowired
   private TemplateEngine templateEngine;



   @Value("${spring.mail.username}")
   private String remetente;

    public String enviarEmail(String destinatario,String texto) {
        try{
            MimeMessage email = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(email, true);


            helper.setTo(destinatario);
            helper.setSubject("Senha Temporária");
            helper.setFrom("App Meu Ônibus<"+remetente+">");

            Context context = new Context();
            context.setVariable("mensagem", texto);


            String html = templateEngine.process("TemplateEmail.html", context);
            helper.setText(html, true);
            mailSender.send(email);





            return "Email Enviado";




        } catch (Exception e) {

            return "Erro ao enviar e-mail"+e.getMessage();

        }

    }
}
