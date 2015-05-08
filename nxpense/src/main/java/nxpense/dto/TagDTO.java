package nxpense.dto;

import java.io.Serializable;
import java.util.List;

public class TagDTO implements Serializable {
    private String name;
    private String backgroundColor;
    private String foregroundColor;

    private List<TagDTO> subTags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public List<TagDTO> getSubTags() {
        return subTags;
    }

    public void setSubTags(List<TagDTO> subTags) {
        this.subTags = subTags;
    }
}
