package net.ooder.vfs.manager;


import net.ooder.vfs.manager.inner.DBFileLink;

public interface FileLinkManager {

    public  void  save(DBFileLink link);

    public void delete(String linkId);

    public DBFileLink createLink();

    public Integer loadAll(Integer pageSize);

    DBFileLink loadById(String objId);




}
