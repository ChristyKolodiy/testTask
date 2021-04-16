package file.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Multimedia {
    TXT(".txt"),
    DOCX(".docx"),
    DOC(".doc"),
    MP3(".mp3"),
    MP4(".mp4"),
    WAV(".wav"),
    JPG(".jpg"),
    JPEG(".jpeg"),
    PNG(".png");

    private String value;

}
