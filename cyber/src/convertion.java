import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class convertion {
	private List<byte[]> list = new ArrayList<byte[]>();
	static public String AESCypher;
	static public String RSACypher;
	static public byte[] SignedEncrypted;
	
	public void singFile() {
		list.removeAll(Collections.singleton(null));
		list.add(AESCypher.getBytes());
		list.add(RSACypher.getBytes());
	}
	
	public void singToFileMD() {
		list.removeAll(Collections.singleton(null));
		list.add(SignedEncrypted);
		list.add(RSACypher.getBytes());
	}
	
	@SuppressWarnings("unused")
	public void writeToFile(String filename) throws FileNotFoundException, IOException {
		File f = new File(filename);
		f.getParentFile().mkdirs();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
	    out.writeObject(list);
		out.close();
		System.out.println("Your file is ready.");
	}
	
	@SuppressWarnings("unchecked")
	public void readOject(String filename) throws Exception {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
		this.list = new ArrayList<byte[]>();
	    this.list = (List<byte[]>) in.readObject();
	    in.close();
	    AESCypher = new String(list.get(0));
	    RSACypher = new String(list.get(1));
	}

	public String getAESCypher() {
		return AESCypher;
	}

	public void setAESCypher(String aESCypher) {
		AESCypher = aESCypher;
	}

	public String getRSACypher() {
		return RSACypher;
	}

	public void setRSACypher(String rSACypher) {
		RSACypher = rSACypher;
	}

	public byte[] getSignedEncrypted() {
		return SignedEncrypted;
	}

	public void setSignedEncrypted(byte[] signedEncrypted) {
		SignedEncrypted = signedEncrypted;
	}
	
	


	

}
