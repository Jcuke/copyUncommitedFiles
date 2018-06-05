import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.impl.FileStatusManagerImpl;

import java.awt.datatransfer.StringSelection;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/6/5.
 */
public class CopyAllChangedFile extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        String projectName = project.getName();
        FileStatusManagerImpl fsm = (FileStatusManagerImpl) FileStatusManager.getInstance(project);
        Class<FileStatusManagerImpl> clzz = FileStatusManagerImpl.class;
        try {
            Field field = clzz.getDeclaredField("a");
            field.setAccessible(true);
            Object obj = field.get(fsm);
            Class<?> innerClzz = Class.forName("java.util.Collections$SynchronizedMap");
            Field innerField = innerClzz.getDeclaredField("m");
            innerField.setAccessible(true);
            HashMap innerObj = (HashMap) innerField.get(obj);
            Set<Map.Entry> entrySet = innerObj.entrySet();
            String msg = "";
            for (Map.Entry entry : entrySet) {

                String path = entry.getKey().toString();
                System.out.println("======================="+path);
                if(!path.contains(".idea") && !path.contains(".iml") && path.contains(".")){
                    obj = entry.getValue();
                    innerClzz = Class.forName("com.intellij.openapi.vcs.FileStatusFactory$FileStatusImpl");
                    innerField = innerClzz.getDeclaredField("myStatus");
                    innerField.setAccessible(true);
                    String status = (String) innerField.get(obj);
                    System.out.println("change status: " + status + ", path: " + path);
                    if(!"NOT_CHANGED".equals(status)){
                        if(path.contains(projectName)){
                            msg += path + "\n";
                        }
                    }
                }
            }
            String s = StringUtil.convertLineSeparators(msg);
            CopyPasteManager.getInstance().setContents(new StringSelection(s));
        } catch (NoSuchFieldException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
    }

}
