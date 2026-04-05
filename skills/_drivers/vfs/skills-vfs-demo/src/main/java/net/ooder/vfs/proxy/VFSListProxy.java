package net.ooder.vfs.proxy;

import net.ooder.common.ConfigCode;
import net.ooder.vfs.manager.inner.DBFolder;
import net.ooder.vfs.manager.inner.EIFileInfo;
import net.ooder.vfs.manager.inner.EIFolder;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

public class VFSListProxy extends AbstractList implements Cloneable,
        Serializable {

    private List eiList;
    private ConfigCode systemCode;

    public VFSListProxy(List eiList) {
        this.eiList = eiList;

    }

    public int size() {
        return eiList.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(Object elem) {
        return indexOf(elem) >= 0;
    }

    public int indexOf(Object elem) {
        return eiList.indexOf(elem);
    }

    public int lastIndexOf(Object elem) {
        return eiList.lastIndexOf(elem);
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray(Object a[]) {
        throw new UnsupportedOperationException();
    }

    public Object get(int index) {
        Object eiObj = eiList.get(index);
        Object result = null;
        if (eiObj instanceof EIFolder) {
            result = new FolderProxy((DBFolder) eiObj,this.systemCode);
        }else if (eiObj instanceof EIFileInfo) {
                result = new FileInfoProxy((EIFileInfo) eiObj,this.systemCode);
        } else
            throw new ClassCastException(
                    "Object's type in the nested list are not recognized: "
                            + eiObj.getClass().getName());
        return result;
    }

    public Object set(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    public void add(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    public Object remove(int index) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException();
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        return false;
    }
}
