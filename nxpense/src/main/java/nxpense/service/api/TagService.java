package nxpense.service.api;


import nxpense.domain.Tag;
import nxpense.dto.TagDTO;

public interface TagService {

    /**
     * Converts the given tag transfer object representation into an entity and saves it in the DB.
     * @param tagDTO representation of the tag item to be created in database
     * @return {@code Tag} entity that has been saved in DB
     */
    public Tag createNewTag(TagDTO tagDTO);
}