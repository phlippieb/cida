/**
 * Copyright (C) 2009
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science
 * University of Pretoria
 * South Africa
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package cs.cirg.cida;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author andrich
 */
public class CIDAInputDialog extends CIDAPromptDialog {

    private String input;
    private String defaultInput;
    private JTextField inputField;

    public CIDAInputDialog(Frame owner, String prompt, String defaultInput) {
        super(owner, prompt);
        this.defaultInput = defaultInput;
    }

    protected class DialogActionListener extends CIDAPromptDialog.DialogActionListener {

        public DialogActionListener() {
            
        }

        private DialogActionListener(CIDAInputDialog dialog) {
            super(dialog);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            input = inputField.getText();
            this.getDialog().dispose();
        }
    }

    @Override
    protected void addComponents() {
        this.add(new JLabel(this.getPrompt()), BorderLayout.NORTH);
        JButton closeButton = new JButton("OK");
        closeButton.addActionListener(new DialogActionListener(this));
        this.add(closeButton, BorderLayout.SOUTH);
        inputField = new JTextField(defaultInput);
        inputField.setFocusable(true);
        this.add(inputField, BorderLayout.CENTER);
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
