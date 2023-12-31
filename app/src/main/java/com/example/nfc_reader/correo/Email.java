package com.example.nfc_reader.correo;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Email {

	private final Properties Properties = new Properties();

	private String contrasena;

	private Session sesion;
/**
 * Inicializacion de las propiedades para el envio de correos
 */
	private void init() {

		Properties.put("mail.smtp.host", "smtp.gmail.com");
		Properties.put("mail.smtp.port", 587);
		Properties.put("mail.smtp.mail.sender", "dreal@unbosque.edu.co");
		Properties.put("mail.smtp.user", "dreal@unbosque.edu.co");
		Properties.put("mail.smtp.auth", "true");
		Properties.put("mail.smtp.starttls.enable", "true");
		contrasena = "wpehnnrqqamagsgy";
		try {
			sesion = Session.getInstance(Properties, new GMailAuthenticator((String) Properties.get("mail.smtp.user"), contrasena));
		}catch (Exception e){
			e.printStackTrace();
		}


	}
/**
 * enviarMail utiliza las propiedades para conectar con el servidor de emails, envia un mensaje personalizado con el usuario y la contraseña .
 *
 * @param nfcContent
 * @param correo
 */
	public void enviarMailLectura(String nfcContent, String correo) {
		init();
		try {
			MimeMessage mensaje = new MimeMessage(sesion);
			mensaje.setFrom(new InternetAddress((String)Properties.get("mail.smtp.mail.sender"), "NoReply"));
			mensaje.setReplyTo(InternetAddress.parse((String)Properties.get("mail.smtp.mail.sender"), false));
			mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo, false));
			mensaje.setSubject("Producto", "UTF-8");
			mensaje.setText("Bienvenid@ al Sistema \nSe ha realizado una Lectura de un Tag con el siguiente contenido:\n\n"+nfcContent+"\n\n\n\nPor favor no responder este correo. Este correo fue enviado de forma automatica.", "UTF-8");
			Transport t = sesion.getTransport("smtp");
			t.connect((String)Properties.get("mail.smtp.host"),(String)Properties.get("mail.smtp.user"),this.contrasena);
			t.send(mensaje);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}


	/**
	 * Método para verificar una dirección de email
	 * @param target Dirección de email a verificar
	 * @return Booleano indicando si es o no una dirección de Email
	 */
	public static boolean isValidEmail(CharSequence target) {
		return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
	}
}
