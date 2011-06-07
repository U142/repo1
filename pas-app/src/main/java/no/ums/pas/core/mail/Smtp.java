package no.ums.pas.core.mail;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;

public class Smtp implements Runnable
{
	public interface smtp_callback
	{
		public void finished();
		public void failed(String e);
	}

	Object mailLock              = null;  //In case we want a multi-threaded mailer 
	public String mailServerHost = "";
	public String from           = "";
	public List<String> to       = null;
	public String replyTo        = "";
	public String subject        = "subject";
	public String mailData       = "mailData";
	public String errorMsg = "";
	public String helo;
	public Socket mailSendSock = null;
	public  BufferedReader inputStream = null;


	public PrintStream outputStream    =  null;
	public String serverReply          = "";
	private smtp_callback callback;
	
	
	public Smtp(String helo, String server,String tFrom,List<String> tTo,String sub,String sendData, smtp_callback callback)
	{
		mailServerHost = server;
        subject = sub;
		mailLock=this; // Thread Monitor passed constructor later. Default this Monitor.
		from = tFrom;
		to   = tTo;
		if(sendData != null)
			mailData = sendData;
		this.callback = callback;
		this.helo = helo;
	}
	
	public boolean open()
	{
	   synchronized(mailLock)
	   {
	      try
	      {
	         mailSendSock = new Socket(mailServerHost, 25);
	         outputStream = new PrintStream(mailSendSock.getOutputStream());
	         inputStream = new BufferedReader(new InputStreamReader(
	          mailSendSock.getInputStream()));
	         serverReply = inputStream.readLine();
	         if(serverReply.startsWith("4"))
	         {
	            errorMsg = "Server refused the connect message : "+serverReply;
	            return false;
	         }
	      }
	      catch(Exception openError)  
	      {
	         openError.printStackTrace();
	         close("Mail Socket Error");
	         return false;
	      }
	      System.out.println("Connected to "+mailServerHost);
	      return true;
	   }
	}
	public void close(String msg)
	{
	          //try to close the sockets
	   System.out.println("Close("+msg+")");
	   try
	   {
	      outputStream.println("quit");
	      inputStream.close();
	      outputStream.close();
	      mailSendSock.close();
	   }
	   catch(Exception e)
	   {
	      System.out.println("Close() Exception");
	     // We are closing so see ya later anyway
	   }
	}

    @Override
	public void run()
	{
        // JavaMail kode
//        try {
//            final Properties props = new Properties();
//            props.put("mail.smtp.host", mailServerHost);
//            Session session = Session.getDefaultInstance(props);
//            session.setDebug(true);
//
//            final MimeMultipart content = new MimeMultipart();
//            final MimeBodyPart body = new MimeBodyPart();
//            body.setText(mailData);
//            content.addBodyPart(body);
//
//            Message msg = new MimeMessage(session);
//            msg.setFrom(new InternetAddress(from));
//
//            for (String rcpt : to) {
//                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(rcpt));
//            }
//
//            msg.setSubject(subject);
//            msg.setContent(content);
//
//            Transport.send(msg);
//        } catch (MessagingException e) {
//            callback.failed(e.getMessage());
//        }

        if(!open())          //Yikes! get out of here.
	      return;     
	   try
	   {
	      outputStream.println("HELO " + helo);
	      serverReply = inputStream.readLine();  
	   }
	   catch(Exception e0)
	   {
	      e0.printStackTrace();
	   }
	   try
	   {
	      outputStream.println("MAIL FROM: "+from);
	      serverReply = inputStream.readLine();
	        // I cheat and don't look for the whole 550
	        // we know 5 is an error anyway. Add it in if you want.
	        //
	      if(serverReply.startsWith("5"))
	      {
	         close("FROM: Server error :"+serverReply);
	         return;
	      }
	        
	   // Note the switch here. we could get mail from somewhere and by
	   // pre setting replyTo reply somewhere else :)

	      if(replyTo == null)
	         replyTo = from;
	      for(String szTo : to)
	    	  outputStream.println("RCPT TO: <"+szTo+">");

	       // Ya got me! I didn't look for any  250 OK messages. Add it in if you really want.
	       // A real programmer will spend 30 hours writing self modifying code in order
	       // to save 90 nano seconds ;)  we assume if it did't give an error it must be OK.
	      serverReply = inputStream.readLine();
	      if(serverReply.startsWith("5"))
	      {
	         close("Reply error:"+serverReply);
	         return;
	      }
	      outputStream.println("DATA");
	      serverReply = inputStream.readLine();
	      if(serverReply.startsWith("5"))
	      {
	         close("DATA Server error : "+serverReply);
	         return;
	      }
	      outputStream.println("From: "+from);
	      outputStream.print("To: ");
	      for(String szTo : to)
	      {
	    	  outputStream.print(szTo + ",");
	      }
	      outputStream.print("\r\n");
	      if(subject != null)
	         outputStream.println("Subject: "+subject);
	      if(replyTo != null)
	         outputStream.println("Reply-to: "+replyTo);
	      outputStream.println("");
	      outputStream.println(mailData);
	      outputStream.print("\r\n.\r\n");
	      outputStream.flush();
	      serverReply = inputStream.readLine();
	      if(serverReply.startsWith("5"))
	      {
	         close("DATA finish server error: "+serverReply);
	         return;
	      }
	      outputStream.println("quit");
	      serverReply = inputStream.readLine();
	      if(serverReply.startsWith("5"))
	      {
	         close("Server error on QUIT: "+serverReply);
	         callback.failed("Server error on QUIT: "+serverReply);
	         return;
	      }
	      inputStream.close();
	      outputStream.close();
	      mailSendSock.close();
	   }
	   catch(Exception e)
	   {
	      e.printStackTrace();
	      close("send() Exception");
	      callback.failed(e.getMessage());
	   }
	   close("Mail sent");
	   callback.finished();
	}
}