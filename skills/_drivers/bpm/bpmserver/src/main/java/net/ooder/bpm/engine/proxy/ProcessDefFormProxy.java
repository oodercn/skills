package net.ooder.bpm.engine.proxy;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.client.ProcessDefForm;
import net.ooder.bpm.engine.database.form.DbProcessDefForm;
import net.ooder.bpm.enums.form.LockEnum;
import net.ooder.bpm.enums.form.MarkEnum;
import net.ooder.common.CommonYesNoEnum;

import java.util.List;


public class ProcessDefFormProxy implements ProcessDefForm {

    private final String systemCode;
    @JSONField(serialize = false)

    private DbProcessDefForm processDefForm;


    public ProcessDefFormProxy(DbProcessDefForm processDefForm, String systemCode) {
        this.systemCode = systemCode;
        this.processDefForm = processDefForm;
    }


    @Override
    public MarkEnum getMark() {
        return processDefForm.getMark();
    }


    @Override
    public LockEnum getLock() {
        return processDefForm.getLock();
    }

    @Override
    public String getProcessDefVersionId() {
        return processDefForm.getProcessDefVersionId();
    }


    @Override
    public CommonYesNoEnum getAutoSave() {
        return processDefForm.getAutoSave();
    }


    @Override
    public CommonYesNoEnum getNoSqlType() {
        return processDefForm.getNoSqlType();
    }


    @Override
    public List<String> getTableNames() {
        return processDefForm.getTableNames();
    }

    @Override
    public List<String> getModuleNames() {
        return processDefForm.getFormsNames();
    }


}
