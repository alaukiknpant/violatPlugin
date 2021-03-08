package plugin.linearizability.violat.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.Tree;
import plugin.linearizability.violat.service.ResultParser;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.util.Map;
import java.util.ResourceBundle;

import static com.intellij.ui.SimpleTextAttributes.STYLE_PLAIN;

public class MainToolWindow {
    private static final Logger log = Logger.getInstance(MainToolWindow.class);

    private JPanel MainToolWindowContent;
    private Tree issueList;

    public MainToolWindow(ToolWindow toolWindow, Project project) {
        issueList.getEmptyText().setText(ResourceBundle.getBundle("strings").getString("no.bug.list.to.show"));
        issueList.setModel(new DefaultTreeModel(null));

        ResultParser.getInstance(project).addPropertyChangeListener(evt -> {
            if(evt.getNewValue() != null && evt.getPropertyName().equals("bugsPerFile")) {
                //noinspection unchecked
                drawBugTree((Map<Integer, String>)evt.getNewValue());
            }
        });

//        //Coloring
        issueList.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                //is a bug or bugtrace (tree depth 3 or 4)
                //is the top most entry (tree depth 1)
                if(row == 0){
                    append(value.toString(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, Color.blue));
                    setIcon(AllIcons.Actions.ListFiles);
                }  else if (((DefaultMutableTreeNode)value).getDepth() == 1) {
                    append(value.toString(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, Color.pink));
                    setIcon(AllIcons.Actions.Preview);

                } else {
                    append(value.toString(), new SimpleTextAttributes(STYLE_PLAIN,Color.gray));
                }
            }
        });

//        issueList.addTreeSelectionListener(e -> ApplicationManager.getApplication().invokeLater(() -> {
//            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
//            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) issueList.getLastSelectedPathComponent();
//
//            if(editor == null || node == null) return;
//            if(node.getUserObject() instanceof ResultListEntry) {
//                final ResultListEntry bug = (ResultListEntry) node.getUserObject();
//                LogicalPosition pos = new LogicalPosition(
//                        bug.getLine() > 0 ? bug.getLine() - 1 : 0,
//                        bug.getColumn() > 0 ? bug.getColumn() - 1 : 0); // -1 because LogicalPosition starts to count at 0;
//                String fileName = bug.getFileName();
//
//                PsiFile[] fileArray = FilenameIndex.getFilesByName(project, fileName , GlobalSearchScope.projectScope(project));
//                if(fileArray.length != 1) {
//                    log.warn("Could not find or to many selected file(s) to navigate to: " + fileName);
//                    return;
//                }
//                fileArray[0].navigate(false);
//
//                editor = FileEditorManager.getInstance(project).getSelectedTextEditor(); //get the new editor because we just changed it
//                if(editor == null) {
//                    log.warn("No editor found. Not jumping to line " + bug.getLine());
//                    return;
//                }
//
//                editor.getScrollingModel().scrollTo(pos, ScrollType.CENTER);
//                editor.getCaretModel().moveToLogicalPosition(pos);
//            }
//        }));

        drawBugTree(ResultParser.getInstance(project).getViolationMap());
    }

    public JPanel getContent() {
        return MainToolWindowContent;
    }

    /**
     * Draws the given bugMap to the Infer Tool Window
     * @param bugMap keys are filenames, while the values are lists of infer bugs
     */
    private void drawBugTree(Map<Integer,String> bugMap) {
        if(bugMap == null) return;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(String.format(ResourceBundle.getBundle("strings").getString("violat.analysis.result.bugs.found"), bugMap.size()));

        for (Map.Entry<Integer, String> entry : bugMap.entrySet()) {
            String[] bugStringList = entry.getValue().split("\n");
            String bugDescription = "";
            if (bugStringList.length >= 2) {
                bugDescription = bugStringList[1];
            }
            DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(String.format(ResourceBundle.getBundle("strings").getString("violat.bugs.found"), entry.getKey(), bugDescription));
//            DefaultMutableTreeNode bugNode = new DefaultMutableTreeNode(entry.getValue());

            for (String elem: bugStringList){
                DefaultMutableTreeNode bugNode = new DefaultMutableTreeNode(elem);
                fileNode.add(bugNode);
            }
            root.add(fileNode);
        }
        TreeModel tm = new DefaultTreeModel(root);
        issueList.setModel(tm);
    }

}