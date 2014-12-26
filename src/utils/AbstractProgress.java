package utils;

import javax.swing.event.EventListenerList;

public abstract class AbstractProgress {

	private EventListenerList listenerList = new EventListenerList();	
	
	public void addProgressEventListener(ProgressEventListener listener) {
		listenerList.add(ProgressEventListener.class, listener);
	}

	public void removeProgressEventListener(ProgressEventListener listener) {
		listenerList.remove(ProgressEventListener.class, listener);
	}

	protected void reportProgress(ProgressEvent evt) {
		if (evt.getProgress() < 0 || evt.getProgress() > 100)
			return;
		
		Object[] listeners = listenerList.getListenerList();

		for (int i = 0; i < listeners.length; i = i+2) {
			if (listeners[i] == ProgressEventListener.class) {
				((ProgressEventListener) listeners[i+1]).reportProgress(evt);
			}
		}
	}
}
