package utils;

@SuppressWarnings("serial")
public class ProgressEvent {
	private int progress;
	private String message;

	public ProgressEvent() {
		
	}

	public ProgressEvent(int progress, String message) {
		this.progress = progress;
		this.message = message;
	}
	
	public void setProgress(int progress)
	{
		this.progress = progress;
	}
	
	public int getProgress()
	{
		return progress;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
}
