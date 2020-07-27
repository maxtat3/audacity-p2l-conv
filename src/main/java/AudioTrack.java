/**
 * POJO from text file described each audio track
 */
public class AudioTrack {

	/**
	 * Duration of each track.
	 * In format: HH:mm
	 * Example: 03:25
	 */
	private String duration;

	/**
	 * Start time positions of track in playlist.
	 */
	private long startTime;

	/**
	 * End time positions of track in playlist.
	 */
	private long endTime;

	/**
	 * Name of this track.
	 */
	private String name;


	public AudioTrack(String duration, String name) {
		this.duration = duration;
		this.name = name;
	}


	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		AudioTrack track = (AudioTrack) obj;

		return duration.equals(track.getDuration())
			&& name.equals(track.getName())
			&& startTime == track.getStartTime()
			&& endTime == track.getEndTime();
	}
}
