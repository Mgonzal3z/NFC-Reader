package com.example.nfc_reader.correo;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class GMailAuthenticator extends Authenticator {
	
	private String user;
	private String psw;
	/**
	 * Autentifica el usuario y la contraseña que permiten el acceso al servidor de emails
	 * @param username
	 * @param password
	 */
	public GMailAuthenticator (String username, String password)
    {
       super();
       this.user = username;
       this.psw = password;
    }
	/**
	 * devuelve el usuario y contraseña ya autentificados.
	 */
   public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(user, psw);
   }

}
