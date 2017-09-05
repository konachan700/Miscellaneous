package uvao_wp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public final class ExceptionSaveEngine implements Thread.UncaughtExceptionHandler {
	private final Thread.UncaughtExceptionHandler PH;

	public ExceptionSaveEngine() {
		PH = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		File f = Environment.getExternalStorageDirectory();
		if (f.canWrite()) {
			final String ef = f.getAbsolutePath() + "/uvao-errors.txt";
			try {
				FileWriter fw = new FileWriter(ef, true);
				StringBuilder sb = new StringBuilder();
				sb.append("Exception: ").append(arg1.getClass().getName())
				                        .append("\n").append("Message: ")
				                        .append(arg1.getMessage())
				                        .append("\nStacktrace:\n");
				
				StackTraceElement[] ste = arg1.getStackTrace();
				for(StackTraceElement element : ste) {
		            sb.append("\t").append(element.toString()).append("\n");
		        }
				
				fw.write(sb.substring(0));
				fw.flush();
				fw.close();
			} catch (IOException e) { }
		}

        if(PH != null) PH.uncaughtException(arg0, arg1);
	}
}
