import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.Cipher;
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
	
	public String choosfile() {
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		int returnValue = jfc.showOpenDialog(null);
		// int returnValue = jfc.showSaveDialog(null);
		String path = "";
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			path = selectedFile.getAbsolutePath();
			return path;
		}else {
			JOptionPane.showMessageDialog(null,
				    "File Not Found.",
				    "Not File",
				    JOptionPane.WARNING_MESSAGE);
			return null;
		}
	}
	
	public void outputSessionMD(String head,String body1,String body2,String body3,String mode,String detail) {
		for (int i = 0; i < 90; i++) {
			main.textArea.append("_");
		}
		main.textArea_2.append("\n\t\t\t< "+head+" >\n");
		main.textArea_2.append("Input File : " + body1 + "\n" );
		main.textArea_2.append("Key : " + body2 +"\n");
		main.textArea_2.append("Detail : "+detail+"\n");
		main.textArea_2.append("Mode : "+mode+"\n" + "Export : " + body3+"\n");
		for (int i = 0; i < 60; i++) {
			main.textArea_2.append("_");
		}
	}
	
	public void fileAES(String key,boolean mode) throws InvalidAlgorithmParameterException {
		try {
			File inputFile = new File(main.textField_8.getText());
			File encryptedFile = new File("MyData/SignedData-encrypted");
			String str_mode = "";
			boolean fien = aes.fileProcessor(Cipher.ENCRYPT_MODE, key, inputFile, encryptedFile, 256);
			str_mode = "AES > ENCRYPT_MODE";
			if (fien == true) {
				System.out.println("complete");
				outputSessionMD("AES Encryption File",inputFile.getName(),key,encryptedFile.getAbsolutePath(),str_mode,
						"การเข้ารหัสไฟล์ AES กับไฟล์ Signature");
			}
		} catch (InvalidKeySpecException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(command.equalsIgnoreCase("Choose FileB1")) {
			String path = choosfile();
			main.textField_5.setText(path);
		}else if(command.equalsIgnoreCase("Choose FileB2")) {
			String path = choosfile();
			this.pathfilePub = path;
			main.textField_6.setText(pathfilePub);
		}else if(command.equalsIgnoreCase("EncryptMD")) {
			
			try {
				Message digiser = new Message(main.textArea_3.getText(), main.textField_5.getText());
				digiser.writeToFile(pathData);
//				Path path = Paths.get(pathData);
//		        Path absolutePath = path.toAbsolutePath();
		        JOptionPane.showMessageDialog(null,
		        	    "Your file Digital Sinature is ready.",
		        	    "successfully",
		        	    JOptionPane.PLAIN_MESSAGE);
//		        Runtime.getRuntime().exec("explorer.exe /select,"+absolutePath.toString());
			} catch (Exception e1) {
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
				String result;
				try {
					result = aes.defile(main.textField_1.getText(),conn.getAESCypher());
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
				JOptionPane.showMessageDialog(null,
					    "Please enter a KeyAES.",
					    "Warning",
					    JOptionPane.WARNING_MESSAGE);
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
			
			try {
				conn.readOject(main.textField_12.getText());
				AsymmetricCryptography rsa = new AsymmetricCryptography();
				PrivateKey privateKey = rsa.getPrivate(main.textField_9.getText());	
				String cypher = conn.getRSACypher();
				String keyaes = rsa.decryptText(cypher,privateKey);
				System.out.println(keyaes);		
				
				printoutput("RSA PrivateKey Decryption", 
						"Result of Decryption: ",
						"KeyAES : "+keyaes, 
						"Cypher : "+cypher, main.textField_9.getText());
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		}else if(command.equalsIgnoreCase("ChooseDataForSend")) {
			String path = choosfile();
			main.textField_12.setText(path);
		}else if(command.equalsIgnoreCase("PubChoose")) {
			String path = choosfile();
			main.textField_2.setText(path);
		}else if(command.equalsIgnoreCase("PriChoose")) {
			String path = choosfile();
			main.textField_9.setText(path);
		}else if(command.equalsIgnoreCase("sessionMD")) {
			try {
				Message digiser = new Message(main.textArea_1.getText(), "MyKeys/privateKey");
				digiser.writeToFile(pathData);
//				Path path = Paths.get(pathData);
//		        Path absolutePath = path.toAbsolutePath();
		        JOptionPane.showMessageDialog(null,
		        	    "Your file Digital Sinature is ready.",
		        	    "successfully",
		        	    JOptionPane.PLAIN_MESSAGE);
		        main.textArea_2.append(">> Your file SignedData.txt signature is ready.\n");
		        //Runtime.getRuntime().exec("explorer.exe /select,"+absolutePath.toString());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(command.equalsIgnoreCase("chooseSig")) {
			String path = choosfile();
			main.textField_8.setText(path);
		}else if(command.equalsIgnoreCase("sessionAESencrypt")){
			if(!main.textField_1.getText().equalsIgnoreCase("")) {
				try {
					System.out.println("setkey");
					fileAES(main.textField_10.getText(),true);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}else {
				try {
					System.out.println("random");
					String ran = aes.generateRandomPassword(8);
					fileAES(ran,true);
					main.textField_10.setText(ran);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}else if(command.equalsIgnoreCase("sessionPubChoose")) {
			String path = choosfile();
			main.textField_4.setText(path);
		}else if(command.equalsIgnoreCase("chooesDataSend")) {
			String path = choosfile();
			main.textField_11.setText(path);
		}else if(command.equalsIgnoreCase("sessionPubKey")){
			try {
				AsymmetricCryptography rsa = new AsymmetricCryptography();
				PublicKey publicKey = rsa.getPublic(main.textField_4.getText());
				String cyPub = rsa.encryptText(main.textField_10.getText(), publicKey);
				
				FileInputStream encryptedfile = new FileInputStream("MyData/SignedData-encrypted");
				byte[] encText = new byte[encryptedfile.available()];
				encryptedfile.read(encText);
				encryptedfile.close();
				conn.setRSACypher(cyPub);
				conn.setSignedEncrypted(encText);
				conn.singToFileMD();
				conn.writeToFile("MyData/DataForSend.txt");
				outputSessionMD("Public Key encryption","SignedData-encrypted","Public Key","DataForSend.txt","RSA > Public Key Encryption"
						,"SignedData-encrypted เข้ารหัสด้วย RSA PublicKey และ เตรียมไฟล์พร้อมส่ง DataForSend.txt");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(command.equalsIgnoreCase("sessionPriChoose")){
			String path = choosfile();
			main.textField_7.setText(path);
		}else if(command.equalsIgnoreCase("sessionRSADecrypt")){
			try {
				conn.readOject(main.textField_11.getText());
				AsymmetricCryptography rsa = new AsymmetricCryptography();
				PrivateKey privateKey = rsa.getPrivate(main.textField_7.getText());	
				String cypher = conn.getRSACypher();
				String keyaes = rsa.decryptText(cypher,privateKey);
				main.textField_3.setText(keyaes);
				System.out.println(keyaes);	
				outputSessionMD("Private Key Decryption",main.textField_11.getText(),"KeyAES : "+keyaes,"","RSA > Private Key Decryption"
						,"ทำการอ่านไฟล์ที่ส่งมาและถอดรหัสเพื่อเอา KeyAES");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(command.equalsIgnoreCase("sessionAESDecrypt")){
			
			if(!main.textField_3.getText().equalsIgnoreCase("")) {
				try {
					String cypher = Base64.getEncoder().encodeToString(conn.getSignedEncrypted());
					System.out.println(main.textField_3.getText()+"\n"+cypher);
					
					byte[] bytes = aes.decryptSing(cypher,main.textField_3.getText(),256);
					FileOutputStream  out = new FileOutputStream("MyData/SignedData-Decrypt.txt");
					out.write(bytes);
//					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("MyData/SignedData-Decrypt.txt"));
//				    out.writeObject(bytes);
					out.close();
					outputSessionMD("Private Key Decryption",main.textField_11.getText(),main.textField_3.getText(),"SignedData-Decrypt.txt","RSA > Private Key Decryption"
							,"นำ KeyAES ไปถอด cypher ที่ได้จากการไฟล์ที่ส่งมา จะได้เป็น SignedData-Decrypt.txt");
					System.out.println("Your file is ready.");
				    
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}else {
				JOptionPane.showMessageDialog(null,
					    "Please enter a KeyAES.",
					    "Warning",
					    JOptionPane.WARNING_MESSAGE);
			}
			
		}else if(command.equalsIgnoreCase("verifySession")){
			try {
				VerifyMessage very = new VerifyMessage("MyData/SignedData-Decrypt.txt", main.textField_4.getText());
				String set = very.getText();
				main.textArea_2.append(set+"\n");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else {
			JOptionPane.showMessageDialog(null,
				    "program is option null",
				    "Error program",
				    JOptionPane.ERROR_MESSAGE);
		}
	}

}
