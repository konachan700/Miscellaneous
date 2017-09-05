package aniwp;

import java.io.FileWriter;
import java.io.IOException;

public final class ExceptionSaveEngine implements Thread.UncaughtExceptionHandler {
	private final Thread.UncaughtExceptionHandler nUncaughtExceptionHandler;

	public ExceptionSaveEngine() {
		nUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		final String rd = FSEngine.GetRootDirectory();
		if (rd == null) return;
	
		final String ef = rd + "errors.log";
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
		
        if(nUncaughtExceptionHandler != null) nUncaughtExceptionHandler.uncaughtException(arg0, arg1);
	}
}
