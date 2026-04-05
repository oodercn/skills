package net.ooder.vfs.manager;

import net.ooder.vfs.FileCopy;
import net.ooder.vfs.VFSException;

public interface FileCopyManager {

    public FileCopy createFileCopy();

    void delete(String copyId) throws VFSException;

    void save(FileCopy copy) throws VFSException;

    public Integer loadAll(Integer pageSize);

    FileCopy loadById(String copyId);


}
