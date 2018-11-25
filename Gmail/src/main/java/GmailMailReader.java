import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GmailMailReader {

    public static void check(String host, String storeType, String user,
                             String password)
    {
        try {

            //create properties field
            Properties properties = new Properties();

            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);

            //create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("pop3s");

            store.connect(host, user, password);

            Folder[] f = store.getDefaultFolder().list();
            for(Folder fd:f){
                System.out.println("-------"+fd.getName()+"------");
            }

            //create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);

            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                System.out.println("---------------------------------");
                System.out.println("Email Number " + (i + 1));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                String textContent = getTextFromMimeMultipart(((MimeMultipart)message.getContent()));
//                String [] importanInformation = getImportantInformation(textContent);
//                for (String line: importanInformation) {
//                    System.out.println(line);
//                }
                System.out.println("Text: " + textContent);
            }

            //close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] getImportantInformation(String textContent) {
        String[] lines = textContent.split(System.getProperty("\n"));
        List<String> result = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            if(lines[i].contains("Forwarded message")) {
                result.add(lines[i+1]);
                result.add(lines[i+2]);
                result.add(lines[i+3]);
                result.add(lines[i+4]);
            }
        }
        return (String[])result.toArray();
    }


    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        String result = "";
        int partCount = mimeMultipart.getCount();
        for (int i = 0; i < partCount; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                // result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
                result = html;
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }

    public static void main(String[] args) {

        String host = "pop.gmail.com";// change accordingly
        String mailStoreType = "pop3";
        String username = "msisgoldteam10@gmail.com";// change accordingly
        String password = "Goldteam12#";// change accordingly

        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");

        check(host, mailStoreType, username, password);

    }

}
