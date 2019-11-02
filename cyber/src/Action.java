import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileSystemView;

public class Action implements ActionListener{
	
	Program main;
	String command;
	String pathfilePri;
	String pathfilePub;
	convertion conn;
	String pathData = "MyData/SignedData.txt";
	String pathCData = "MyData/cypherAES.txt";
	String pathCRsa = "MyData/cypherRSA.txt";
	SymmetricCryptography aes = new SymmetricCryptography();
	
	public Action(Program main,String command, convertion conn) {
		this.main = main;
		this.command = command;
		this.conn = conn;
	}
	
	public void printoutput(String head,String label,String body,String text,String key) {
		main.textArea.append("\n\t\t\t\t\t"+head+"\n");
		main.textArea.append("\t"+text+"\n");
		main.textArea.append("\tKey : "+key+"\n");
		main.textArea.append("\t"+label+"\n");
		main.textArea.append("\t"+body+"\n");
		main.textArea.append("\n\t\t\t\t\t***************\n");
		for (int i = 0; i < 90; i++) {
			main.textArea.append("_");
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(command.equalsIgnoreCase("Choose FileB1")) {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			int returnValue = jfc.showOpenDialog(null);
			// int returnValue = jfc.showSaveDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = jfc.getSelectedFile();
				pathfilePri = selectedFile.getAbsolutePath();
				main.textField_5.setText(pathfilePri);
				System.out.println(pathfilePri);
			}
		}else if(command.equalsIgnoreCase("Choose FileB2")) {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			int returnValue = jfc.showOpenDialog(null);
			// int returnValue = jfc.showSaveDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = jfc.getSelectedFile();
				this.pathfilePub = selectedFile.getAbsolutePath();
				main.textField_6.setText(pathfilePub);
				System.out.println(pathfilePub);
			}
		}else if(command.equalsIgnoreCase("EncryptMD")) {
			
			try {
				Message digiser = new Message(main.textArea_3.getText(), main.textField_5.getText());
				digiser.writeToFile(pathData);
				Path path = Paths.get(pathData);
		        Path absolutePath = path.toAbsolutePath();
		        JOptionPane.showMessageDialog(null,
		        	    "Your file Digital Sinature is ready.",
		        	    "successfully",
		        	    JOptionPane.PLAIN_MESSAGE);
		        Runtime.getRuntime().exec("explorer.exe /select,"+absolutePath.toString());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(command.equalsIgnoreCase("DecryptMD")) {
			try {
				VerifyMessage very = new VerifyMessage(pathData, main.textField_6.getText());
				String set = very.getText();
				main.textArea_4.setText(set);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(command.equalsIgnoreCase("AESEncrypt")) {
			
			if(!main.textField_1.getText().equalsIgnoreCase("")) {
				try {
					System.out.println("setkey");
					aes.secureKet(main.textField.getText(), main.textField_1.getText());
					String cypher = aes.getEncypher();
					
					printoutput("AES encryption",
							"Result of encryption in base64:"
							,cypher, main.textField.getText()
							,"CyPher: "+main.textField_1.getText());
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}else {
				try {
					System.out.println("random");
					aes.randomKey(main.textField.getText());
					String cypher = aes.getEncypher();
					conn.setAESCypher(cypher);
					System.out.println(cypher);
					
					printoutput("AES encryption",
							"Result of encryption in base64:",
							"CyPher: "+cypher, "Original Text : "
							+main.textField.getText()
							,aes.getkey());
					
					main.textField_1.setText(aes.getkey());
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}else if(command.equalsIgnoreCase("AESDecrypt")) {
			
			if(!main.textField_1.getText().equalsIgnoreCase("")) {
				System.out.println("-");
				String result;
				try {
					result = aes.defile(main.textField_1.getText(),main.textField.getText());
					System.out.println(result);
					
					printoutput("AES Decryption", 
							"Result of Decryption : ",
							"Original Text: "+result, 
							"CyPher: "+main.textField.getText(), 
							main.textField_1.getText());
					
					main.textField_1.setText(aes.getkey());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}else {

			}
			
		}else if(command.equalsIgnoreCase("PubEnc")) {
	
			try {
				AsymmetricCryptography rsa = new AsymmetricCryptography();
				PublicKey publicKey = rsa.getPublic(main.textField_2.getText());
				String cyPub = rsa.encryptText(main.textField_1.getText(), publicKey);
				conn.setRSACypher(cyPub);
				printoutput("RSA PublicKey encryption"
						,"Result of encryption in base64:"
						,"Cypher : "+cyPub, 
						"KeyAES : "+main.textField_1.getText(),
						main.textField_2.getText());
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		}else if(command.equalsIgnoreCase("PriDec")) {
			
			String aescypher = conn.getAESCypher();
			String rsacypher = conn.getRSACypher();
			try {
				AsymmetricCryptography rsa = new AsymmetricCryptography();
				PrivateKey privateKey = rsa.getPrivate(main.textField_9.getText());	
				String cypher = main.textField.getText();
				String keyaes = rsa.decryptText(cypher,privateKey);
				System.out.println(keyaes);		
				
				printoutput("RSA PrivateKey Decryption", 
						"Result of Decryption: ",
						"KeyAES : "+keyaes, 
						"Cypher : "+cypher, main.textField_9.getText());
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		}else if(command.equalsIgnoreCase("PubChoose")) {
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			int returnValue = jfc.showOpenDialog(null);
			// int returnValue = jfc.showSaveDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = jfc.getSelectedFile();
				main.textField_2.setText(selectedFile.getAbsolutePath());
			}
		}else if(command.equalsIgnoreCase("PriChoose")) {
			
			try {
				conn.readOject("MyData/DataForSend.txt");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			int returnValue = jfc.showOpenDialog(null);
			// int returnValue = jfc.showSaveDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File selectedFile = jfc.getSelectedFile();
				main.textField_9.setText(selectedFile.getAbsolutePath());
			}
		}
		
		
	}

}
