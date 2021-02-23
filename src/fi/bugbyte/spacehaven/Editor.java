package fi.bugbyte.spacehaven;

import com.badlogic.gdx.utils.Array;
import fi.bugbyte.framework.Game;
import fi.bugbyte.spacehaven.gui.GUI;
import fi.bugbyte.spacehaven.stuff.Character;
import fi.bugbyte.spacehaven.stuff.Production;
import fi.bugbyte.spacehaven.stuff.personality.Personality;
import fi.bugbyte.spacehaven.world.Ship;
import fi.bugbyte.spacehaven.world.elements.Storage;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class Editor {
    private Ship ship;
    private boolean shipSet = false;
    private MyStorageTableModel storageData;
    private MyCharacterDataModel characterData;

    public Editor() {
        storageTable.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                    String test = "test";
            }
        });

        initializeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!shipSet) {
                    Array<Ship> ships =  GUI.instance.getWorld().getShips();
                    for (Ship ship1 : ships) {
                        if (ship1.isPlayerShip()) {
                            ship = ship1;
                            break;
                        }
                    }

                    Array<Character> characters = ship.getCharacters();

                    Array<Storage.ItemStorage> storages = ship.getStorage();
                    Storage.ItemStorage storage = storages.get(0);
                    Storage.Inventory t = storage.getInventory();
                    Array<Storage.StoredItem> items = t.getStuff();
//                  characters.get(selected).getPersonality().getProperties(); //Accuracy, Workspeed, AccidentRate, LearningRate, RecoverRate, SurrenderRate

                    storageSelector.setModel(new ComboBoxModel() {
                        int selected;

                        @Override
                        public int getSize() {
                            return storages.size;
                        }

                        @Override
                        public Object getElementAt(int index) {
                            return index;
                        }

                        @Override
                        public void addListDataListener(ListDataListener l) {

                        }

                        @Override
                        public void removeListDataListener(ListDataListener l) {

                        }

                        @Override
                        public void setSelectedItem(Object anItem) {
                            selected = (int) anItem;
                            if (storageData != null) {
                                storageData.items = storages.get(selected).getInventory().getStuff();
                                storageTable.updateUI();
                            }
                        }

                        @Override
                        public Object getSelectedItem() {
                            return selected;
                        }
                    });

                    characterSelector.setModel(new ComboBoxModel() {
                        int selected = 0;
                        String selectedName;

                        @Override
                        public int getSize() {
                            return characters.size;
                        }

                        @Override
                        public Object getElementAt(int index) {
                            selected = index;
                            selectedName = characters.get(index).getName();
                            return selectedName;
                        }

                        @Override
                        public void addListDataListener(ListDataListener l) {

                        }

                        @Override
                        public void removeListDataListener(ListDataListener l) {

                        }

                        @Override
                        public void setSelectedItem(Object anItem) {
                            if (characterData != null) {
                                characterData.skills = characters.get(selected).getPersonality().getSkills();
                                characterTable.updateUI();
                            }
                        }

                        @Override
                        public Object getSelectedItem() {
                            return selectedName;
                        }
                    });

                    storageData = new MyStorageTableModel(items);
                    characterData = new MyCharacterDataModel(characters.get(0).personality.getSkills());

                    storageTable.setModel(storageData);
                    characterTable.setModel(characterData);

                    shipSet = true;

                    DefaultTableCellRenderer alignedRenderer = new DefaultTableCellRenderer();
                    alignedRenderer.setHorizontalAlignment(JLabel.RIGHT);

//                    alignedRenderer.setFont(new Font(alignedRenderer.getFont().getName(), Font.BOLD, 14));

                    storageTable.getColumnModel().getColumn(0).setCellRenderer(alignedRenderer);
                    characterTable.getColumnModel().getColumn(0).setCellRenderer(alignedRenderer);

//                    characterTable.setFont(new Font(alignedRenderer.getFont().getName(), Font.BOLD, 14));

                    storageTable.setRowHeight(18);
                    characterTable.setRowHeight(18);

                    btnStorage.addActionListener(e1 -> storageData.setMinimumValuesTo25());
                    btnSkill.addActionListener(e1 -> characterData.setLevelsTo10());
                }
            }
        });
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        MainClass.main(new String[] {""});
        Thread.sleep(1000);
//        new Thread(new Runnable() {
//            public void run() {
                JFrame frame = new JFrame("Editor");
                frame.setContentPane(new Editor().panel1);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
//            }
//        }).start();

//        Thread.sleep(2000);
//        MainClass.main(new String[] {""});
    }

    private JPanel panel1;
    private JTable storageTable;
    private JComboBox storageSelector;
    private JButton initializeBtn;
    private JComboBox characterSelector;
    private JTable characterTable;
    private JButton btnStorage;
    private JButton btnSkill;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private static class MyStorageTableModel extends AbstractTableModel {
        private Array<Storage.StoredItem> items;
        private final String[] columnNames = {"Element Name","Amount"};
        private final HashMap<Integer, String> idToName = new HashMap<>();

        public MyStorageTableModel(Array<Storage.StoredItem> t) {
            items = t;
        }

        @Override
        public int getRowCount() {
            return items.size;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            } else {
                return Integer.class;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                int i = items.get(rowIndex).elementaryId;
                String s = idToName.get(i);
                if (s == null) {
                    Production.Elementary t = SpaceHaven.library.getElementary(i);
                    if (t != null) {
                        s = Game.library.getTextById(t.name.id).getText();
                    } else {
                        s = Integer.toString(i);
                    }
                    idToName.put(i, s);
                }
                return s;
            } else {
                return items.get(rowIndex).inStorage;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            try {
                items.get(rowIndex).inStorage = (Integer) aValue;
                fireTableCellUpdated(rowIndex, columnIndex);
            } catch (Exception ignored) {

            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 1;
        }

        public static boolean isInteger(String s) {
            if(s.isEmpty()) return false;
            for(int i = 0; i < s.length(); i++) {
                if(i == 0 && s.charAt(i) == '-') {
                    if(s.length() == 1) return false;
                    else continue;
                }
                if(java.lang.Character.digit(s.charAt(i),10) < 0) return false;
            }
            return true;
        }

        public void setMinimumValuesTo25() {
            for (Storage.StoredItem item : items) {
                int i = item.elementaryId;
                boolean b = isInteger(idToName.get(i));
                if (!b) {
                    if (item.inStorage < 25)
                        item.inStorage = 25;
                }
            }
            this.fireTableDataChanged();
        }
    }

    private static class MyCharacterDataModel extends AbstractTableModel {
        private Array<Personality.Skill> skills;
        private final String[] columnNames = {"Skill","Level"};

        public MyCharacterDataModel( Array<Personality.Skill> t) {
            skills = t;
        }

        @Override
        public int getRowCount() {
            return skills.size;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return String.class;
            } else {
                return Integer.class;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return skills.get(rowIndex).skill.getName();
            } else {
                return skills.get(rowIndex).getLevel();
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            try {
                Integer i = (Integer) aValue;
                skills.get(rowIndex).setLevel(i);
                fireTableCellUpdated(rowIndex, columnIndex);
            } catch (Exception ignored) {

            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 1;
        }

        public void setLevelsTo10() {
            for (Personality.Skill skill : skills) {
                skill.level = 10;
            }
            this.fireTableDataChanged();
        }
    }
}
