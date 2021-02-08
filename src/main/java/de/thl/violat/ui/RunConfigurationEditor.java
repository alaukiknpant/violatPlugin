package de.thl.violat.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.JBColor;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.fields.ExpandableTextField;


import de.thl.violat.config.GlobalSettings;
import de.thl.violat.config.PluginConfigurable;
import de.thl.violat.model.Checker;
import de.thl.violat.model.ViolatInstallation;
import de.thl.violat.model.ViolatVersion;
import de.thl.violat.model.buildtool.BuildTool;
import de.thl.violat.run.ViolatRunConfiguration;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ResourceBundle;

public class RunConfigurationEditor extends SettingsEditor<ViolatRunConfiguration> {
    private JPanel mainPanel;
    private JComboBox<ViolatInstallation> violatInstallationComboBox;
    private JComboBox<BuildTool> usingBuildToolComboBox;
    private ExpandableTextField additionalArgsTextField;
//    private JBCheckBox reactiveModeJBCheckBox;
    private JBPanel installPanel;
    private JPanel checkersJPanel;
    private JBList<Checker> checkersJBList;
    private CollectionListModel<Checker> checkersListModel;
    private ViolatVersion selectedVersion; //the current selected version of the infer installation

   //paths
    private TextFieldWithBrowseButton artifactPathChooser;
    private TextFieldWithBrowseButton JSONpathChooser;

    //path booleans
    private boolean artifactModified = false;
    private boolean JSONModified = false;

    // buttons
    private JButton checkAndAddSpecsButton;
    private JButton checkAndAddArtifactsButton;



    public RunConfigurationEditor() {
        this.installPanel.setLayout(new OverlayLayout(this.installPanel));

        //Reset the Checker list to default if installation was changed
        violatInstallationComboBox.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                if(selectedVersion == null) return; //this happens only when the form was just initialized, so we dont need to do anything

                checkersListModel.replaceAll(Checker.getDefaultCheckers());

                selectedVersion = ((ViolatInstallation)itemEvent.getItem()).getVersion();
            }
        });

        // artifact path chooser

        artifactPathChooser.addActionListener(e -> FileChooser.chooseFile(
                FileChooserDescriptorFactory.createAllButJarContentsDescriptor(),
                ProjectUtil.guessCurrentProject(mainPanel),
                LocalFileSystem.getInstance().findFileByPath(artifactPathChooser.getText().isEmpty() ? "/" : artifactPathChooser.getText()), //where the file chooser starts
                (dir) -> artifactPathChooser.setText(dir.getPath())));

        artifactPathChooser.getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {artifactModified = true;}
            @Override public void removeUpdate(DocumentEvent e) {artifactModified = true;}
            @Override public void changedUpdate(DocumentEvent e) {artifactModified = true;}
        });

        // Specs path chooser

        JSONpathChooser.addActionListener(e -> FileChooser.chooseFile(
                FileChooserDescriptorFactory.createSingleFileDescriptor(),
                ProjectUtil.guessCurrentProject(mainPanel),
                    LocalFileSystem.getInstance().findFileByPath(JSONpathChooser.getText().isEmpty() ? "/" : JSONpathChooser.getText()), //where the file chooser starts
                    (dir) -> JSONpathChooser.setText(dir.getPath())));

        JSONpathChooser.getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) {JSONModified = true;}
            @Override public void removeUpdate(DocumentEvent e) {JSONModified = true;}
            @Override public void changedUpdate(DocumentEvent e) {JSONModified = true;}
        });


        //Clicked Add Installation Button
        checkAndAddSpecsButton.addActionListener(e -> {
            final boolean success = GlobalSettings.getInstance().addJsonSpecs(JSONpathChooser.getText());
            if(!success) showSpecsAddInstallationError();
            else showPathAddedSucessfully(JSONpathChooser, checkAndAddSpecsButton);
//            System.out.println(JSONpathChooser.getText());
//            System.out.println("\n\n\n");
        });

        checkAndAddArtifactsButton.addActionListener(e -> {
            final boolean success = GlobalSettings.getInstance().addArtifact(artifactPathChooser.getText());
            if(!success) showArtifactAddInstallationError();
            else showPathAddedSucessfully(artifactPathChooser, checkAndAddArtifactsButton);
//            System.out.println(JSONpathChooser.getText());
//            System.out.println("\n\n\n");
        });

        // We do not have a JB box yet
//        showInferConsoleJBCheckBox.addActionListener(e -> artifactModified = true);

    }

//    private void refreshInstallationList() {
//        ((DefaultListModel<ViolatInstallation>) this.installationJBList.getModel()).clear();
//        for(ViolatInstallation ii : GlobalSettings.getInstance().getInstallations()) {
//            ((DefaultListModel<ViolatInstallation>) this.installationJBList.getModel()).addElement(ii);
//        }
//    }

    private void showArtifactAddInstallationError() {
        final Color oldBg = artifactPathChooser.getBackground();
        artifactPathChooser.setBackground(JBColor.red);
        checkAndAddArtifactsButton.setEnabled(false);
        artifactPathChooser.setText(ResourceBundle.getBundle("strings").getString("invalid.artifact.selected"));
        Timer timer = new Timer(3000, actionEvent -> {
            artifactPathChooser.setBackground(oldBg);
            checkAndAddArtifactsButton.setEnabled(true);
            artifactPathChooser.setText("");
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showSpecsAddInstallationError() {
        final Color oldBg = JSONpathChooser.getBackground();
        JSONpathChooser.setBackground(JBColor.red);
        checkAndAddArtifactsButton.setEnabled(false);
        JSONpathChooser.setText(ResourceBundle.getBundle("strings").getString("invalid.spec.selected"));
        Timer timer = new Timer(3000, actionEvent -> {
            JSONpathChooser.setBackground(oldBg);
            checkAndAddArtifactsButton.setEnabled(true);
            JSONpathChooser.setText("");
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showPathAddedSucessfully(TextFieldWithBrowseButton pathChooser, JButton button) {
        final Color oldBg = pathChooser.getBackground();
        pathChooser.setBackground(JBColor.green);
        button.setEnabled(false);
//        pathChooser.setText(ResourceBundle.getBundle("strings").getString("path.added.sucessfully"));
        Timer timer = new Timer(3000, actionEvent -> {
//            JSONpathChooser.setBackground(oldBg);
            button.setEnabled(true);
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    protected void resetEditorFrom(@NotNull ViolatRunConfiguration inferRC) {
        reloadBuildToolComboBoxList(inferRC);
//        additionalArgsTextField.setText(inferRC.getLaunchOptions().getAdditionalArgs());
        this.checkersListModel.replaceAll(inferRC.getLaunchOptions().getSelectedCheckers());
//        this.reactiveModeJBCheckBox.setSelected(inferRC.getLaunchOptions().isReactiveMode());
        reloadInstallationComboBox(inferRC);

        this.selectedVersion = inferRC.getLaunchOptions().getSelectedInstallation().getVersion();
    }

    @Override
    protected void applyEditorTo(@NotNull ViolatRunConfiguration violatRC) {
        if(this.violatInstallationComboBox.isEnabled()) violatRC.getLaunchOptions().setSelectedInstallation((ViolatInstallation) this.violatInstallationComboBox.getSelectedItem());
//        violatRC.getLaunchOptions().setUsingBuildTool((BuildTool) usingBuildToolComboBox.getSelectedItem());
//        violatRC.getLaunchOptions().setAdditionalArgs(additionalArgsTextField.getText());
        violatRC.getLaunchOptions().setSelectedCheckers(this.checkersListModel.toList());
//        violatRC.getLaunchOptions().setReactiveMode(this.reactiveModeJBCheckBox.isSelected());

        this.selectedVersion = violatRC.getLaunchOptions().getSelectedInstallation().getVersion();
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return mainPanel;
    }

    private void reloadBuildToolComboBoxList(ViolatRunConfiguration violatRC) {
        usingBuildToolComboBox.setModel(new DefaultComboBoxModel<>(
                violatRC.getLaunchOptions().getAvailableBuildTools().toArray(new BuildTool[0])
        ));

//        if(violatRC.getLaunchOptions().getUsingBuildTool() == null) usingBuildToolComboBox.setSelectedItem(BuildToolFactory.getPreferredBuildTool(violatRC.getProject()));
//        else usingBuildToolComboBox.setSelectedItem(violatRC.getLaunchOptions().getUsingBuildTool());
    }

    private void reloadInstallationComboBox(ViolatRunConfiguration inferRC) {
        violatInstallationComboBox.setModel(
                new DefaultComboBoxModel<>(
                        GlobalSettings.getInstance().getInstallations().toArray(new ViolatInstallation[0])
                ));
        violatInstallationComboBox.setEnabled(true);
        violatInstallationComboBox.setVisible(true);

        if(violatInstallationComboBox.getItemCount() > 0 && inferRC.getLaunchOptions().getSelectedInstallation() != null) {
            violatInstallationComboBox.setSelectedItem(inferRC.getLaunchOptions().getSelectedInstallation());
        }
        //Show the clickable warning if no Installation is configured
        if(violatInstallationComboBox.getItemCount() == 0) {
            //create the warning label only if it doesnt exist (= only one other component in the installPanel)
            if(installPanel.getComponentCount() == 1) {
                final JBLabel warningLabel = new JBLabel(ResourceBundle.getBundle("strings").getString("warning.no.valid.installation.found.click.here.to.add.one"));
                warningLabel.setForeground(new JBColor(0x5106FF, 0x0652FF));
                warningLabel.setHorizontalAlignment(SwingConstants.CENTER);
                warningLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        super.mouseClicked(e);
                        ShowSettingsUtil.getInstance().showSettingsDialog(inferRC.getProject(), PluginConfigurable.class);
                    }
                });
                installPanel.add(warningLabel);
                installPanel.revalidate();
            }
            violatInstallationComboBox.setVisible(false);
            violatInstallationComboBox.setEnabled(false);
        }
    }

//    private void getNewCheckerList(Checker selectedChecker, CollectionListModel<Checker> checkersListModel) {
//        if (checkersListModel.getSize() == 0) {
//            checkersListModel.add(selectedChecker);
//        } else {
//            System.out.println("Checker can have 1 item max");
//         }
//        }

    private void checkersAddAction(final AnActionButton button) {
        if(violatInstallationComboBox.getSelectedItem() == null) return;

        final ViolatVersion usingVersion = ((ViolatInstallation) violatInstallationComboBox.getSelectedItem()).getVersion().isValid() ?
                ((ViolatInstallation) violatInstallationComboBox.getSelectedItem()).getVersion() :
                new ViolatVersion(1, 16, 0); //fallback to oldest supported version if no valid version is selected
        final List<Checker> notSelectedCheckers = Checker.getMissingCheckers(checkersListModel.getItems(), usingVersion);

        JBPopupFactory.getInstance()
                .createPopupChooserBuilder(notSelectedCheckers)
                .setTitle("Add Checker")
                .setItemChosenCallback((selectedChecker) -> {
                            if (checkersListModel.getSize() == 0) {
                                checkersListModel.add(selectedChecker);
                            } else {
                                System.out.println("Checker can have 1 item max");
                                checkersListModel.add(selectedChecker);
                                // Option to create some sort of Pop Up
//                                createCheckerFullPopup().show(button.getPreferredPopupPoint());
                            }
                        }).createPopup().show(button.getPreferredPopupPoint());

    }



    private void checkersRemoveAction(final AnActionButton button) {
        final List<Checker> selectedCheckers = checkersJBList.getSelectedValuesList();
        for(Checker checker : selectedCheckers) {
            checkersListModel.remove(checker);
        }
    }

    /**
     * Creates UI components, which cannot be created by the ui designer
     */
    private void createUIComponents() {
        this.checkersListModel = new CollectionListModel<>();
        this.checkersJBList = new JBList<>(this.checkersListModel);
        this.checkersJBList.setEmptyText(ResourceBundle.getBundle("strings").getString("no.checkers.selected"));

        ToolbarDecorator td = ToolbarDecorator
                .createDecorator(checkersJBList)
                .setAddAction(this::checkersAddAction)
                .setRemoveAction(this::checkersRemoveAction)
                .addExtraAction(new AnActionButton(ResourceBundle.getBundle("strings").getString("reset.to.default"), AllIcons.General.TodoDefault) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        checkersListModel.replaceAll(Checker.getDefaultCheckers());
                    }
                })
                .disableUpDownActions();
        checkersJPanel = td.createPanel();
    }
}
