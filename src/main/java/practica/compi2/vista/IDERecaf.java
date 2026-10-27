package practica.compi2.vista;

import practica.compi2.vista.controllers.CompileController;
import practica.compi2.vista.controllers.EditorController;
import practica.compi2.vista.controllers.FolderController;
import practica.compi2.vista.controllers.ProjectController;
import practica.compi2.vista.editor.CaretStatus;

public class IDERecaf extends javax.swing.JFrame {
    private final IdeConsole console;
    private final EditorController editorController;
    private final ProjectController projectController;
    private final CompileController compileController;
    private final FolderController folderController;

    public IDERecaf() {
        initComponents();
        addStatusBar();
        setLocationRelativeTo(null);
        setTitle("IDE");
        console = new IdeConsole(jTextArea1);
        editorController = new EditorController(this, jTextPane1, console);
        projectController = new ProjectController(this, treeProyecto, editorController, console);
        compileController = new CompileController(this, editorController, projectController, console);
        folderController = new FolderController(this, projectController, editorController, console);
        compileButton.addActionListener(evt -> compileController.compile());
        downloadButton.addActionListener(evt -> compileController.downloadCode());
        addRiscVMenus();
    }

    private void addStatusBar() {
        javax.swing.JLabel positionLabel = new javax.swing.JLabel();
        positionLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        positionLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 10, 3, 10));
        new CaretStatus(jTextPane1, positionLabel);
        javax.swing.JPanel root = new javax.swing.JPanel(new java.awt.BorderLayout());
        root.add(getContentPane(), java.awt.BorderLayout.CENTER);
        root.add(positionLabel, java.awt.BorderLayout.SOUTH);
        setContentPane(root);
        pack();
    }

    private void addRiscVMenus() {
        javax.swing.JMenuItem runRiscVButton = new javax.swing.JMenuItem("Ejecutar RISC-V (simulador)");
        runRiscVButton.addActionListener(evt -> compileController.runRiscV());
        jMenu2.add(runRiscVButton);
        javax.swing.JMenuItem downloadRiscVButton = new javax.swing.JMenuItem("Codigo RISC-V");
        downloadRiscVButton.addActionListener(evt -> compileController.downloadRiscV());
        jMenu3.add(downloadRiscVButton);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        treeProyecto = new javax.swing.JTree();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        newProjectButton = new javax.swing.JMenuItem();
        newFileButton = new javax.swing.JMenuItem();
        openProjectButton = new javax.swing.JMenuItem();
        openFileButton = new javax.swing.JMenuItem();
        saveButton = new javax.swing.JMenuItem();
        saveAsButton = new javax.swing.JMenuItem();
        closeProjectButton = new javax.swing.JMenuItem();
        descargarFileButton = new javax.swing.JMenuItem();
        descargarCarpetaButton = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        compileButton = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        downloadButton = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setViewportView(treeProyecto);

        jScrollPane2.setViewportView(jTextPane1);

        jLabel1.setText("CONSOLE");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        jButton1.setText("Compilar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jMenu1.setText("Opciones");

        newProjectButton.setText("Nueva Carpeta");
        newProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProjectButtonActionPerformed(evt);
            }
        });
        jMenu1.add(newProjectButton);

        newFileButton.setText("Nuevo Archivo");
        newFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newFileButtonActionPerformed(evt);
            }
        });
        jMenu1.add(newFileButton);

        openProjectButton.setText("Abrir Proyecto");
        openProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openProjectButtonActionPerformed(evt);
            }
        });
        jMenu1.add(openProjectButton);

        openFileButton.setText("Abrir Archivo");
        openFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileButtonActionPerformed(evt);
            }
        });
        jMenu1.add(openFileButton);

        saveButton.setText("Guardar");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jMenu1.add(saveButton);

        saveAsButton.setText("Guardar Como");
        saveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsButtonActionPerformed(evt);
            }
        });
        jMenu1.add(saveAsButton);

        closeProjectButton.setText("Cerrar Proyecto");
        closeProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeProjectButtonActionPerformed(evt);
            }
        });
        jMenu1.add(closeProjectButton);

        descargarFileButton.setText("Descargar Archivo");
        descargarFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descargarFileButtonActionPerformed(evt);
            }
        });
        jMenu1.add(descargarFileButton);

        descargarCarpetaButton.setText("Descargar Carpeta");
        descargarCarpetaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descargarCarpetaButtonActionPerformed(evt);
            }
        });
        jMenu1.add(descargarCarpetaButton);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Compilar");

        compileButton.setText("Codigo en Tres Direcciones");
        jMenu2.add(compileButton);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Descargar");

        downloadButton.setText("Codigo En Tres Direcciones");
        jMenu3.add(downloadButton);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("Console");

        jMenuItem1.setText("Clean Console");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem1);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(986, 986, 986)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 758, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jButton1))
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProjectButtonActionPerformed
        folderController.createFolder();
    }//GEN-LAST:event_newProjectButtonActionPerformed

    private void openProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openProjectButtonActionPerformed
        projectController.openProject();
    }//GEN-LAST:event_openProjectButtonActionPerformed

    private void newFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newFileButtonActionPerformed
        projectController.createFile();
    }//GEN-LAST:event_newFileButtonActionPerformed

    private void closeProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeProjectButtonActionPerformed
        projectController.closeProject();
    }//GEN-LAST:event_closeProjectButtonActionPerformed

    private void openFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileButtonActionPerformed
        editorController.openWithChooser(projectController.getProjectFolder());
    }//GEN-LAST:event_openFileButtonActionPerformed

    private void saveAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
        editorController.saveAs();
    }//GEN-LAST:event_saveAsButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        editorController.save();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        compileController.compile();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        jTextArea1.setText("");
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void descargarFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descargarFileButtonActionPerformed
        folderController.downloadFile();
    }//GEN-LAST:event_descargarFileButtonActionPerformed

    private void descargarCarpetaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descargarCarpetaButtonActionPerformed
        folderController.downloadFolder();
    }//GEN-LAST:event_descargarCarpetaButtonActionPerformed

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.err.println("No se pudo aplicar el estilo Nimbus: " + ex.getMessage());
        }
        java.awt.EventQueue.invokeLater(() -> new IDERecaf().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem closeProjectButton;
    private javax.swing.JMenuItem compileButton;
    private javax.swing.JMenuItem descargarCarpetaButton;
    private javax.swing.JMenuItem descargarFileButton;
    private javax.swing.JMenuItem downloadButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JMenuItem newFileButton;
    private javax.swing.JMenuItem newProjectButton;
    private javax.swing.JMenuItem openFileButton;
    private javax.swing.JMenuItem openProjectButton;
    private javax.swing.JMenuItem saveAsButton;
    private javax.swing.JMenuItem saveButton;
    private javax.swing.JTree treeProyecto;
    // End of variables declaration//GEN-END:variables
}
