package com.dnamaster10.traincartsticketshop.util.database.accessors;

import com.dnamaster10.traincartsticketshop.util.database.databaseobjects.LinkDatabaseObject;
import com.dnamaster10.traincartsticketshop.util.exceptions.ModificationException;
import com.dnamaster10.traincartsticketshop.util.exceptions.QueryException;
import com.dnamaster10.traincartsticketshop.util.database.DatabaseAccessorFactory;
import com.dnamaster10.traincartsticketshop.util.database.dbaccessorinterfaces.LinksDatabaseAccessor;

import java.util.List;

public class LinkDataAccessor extends DataAccessor {
    LinksDatabaseAccessor linksDatabaseAccessor = DatabaseAccessorFactory.getLinksDatabaseAccessor();

    public LinkDatabaseObject[] getLinks(int guiId, int page) throws QueryException {
        return linksDatabaseAccessor.getLinksByGuiId(guiId, page);
    }

    public LinkDatabaseObject[] searchLinks(int guiId, int offset, String searchTerm) throws QueryException {
        return linksDatabaseAccessor.searchLinks(guiId, offset, searchTerm);
    }

    public int getTotalLinks(int guiId) throws QueryException {
        return linksDatabaseAccessor.getTotalLinks(guiId);
    }

    public int getTotalLinkSearchResults(int guiId, String searchTerm) throws QueryException {
        return linksDatabaseAccessor.getTotalLinkSearchResults(guiId, searchTerm);
    }

    public void saveLinkPage(int guiId, int page, List<LinkDatabaseObject> links) throws ModificationException {
        linksDatabaseAccessor.saveLinkPage(guiId, page, links);
    }
}
