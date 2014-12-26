package utils;

import java.util.EventListener;

public interface ProgressEventListener extends EventListener
{
	public void reportProgress(ProgressEvent evt);
}
