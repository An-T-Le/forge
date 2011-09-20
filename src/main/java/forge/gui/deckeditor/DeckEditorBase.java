package forge.gui.deckeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.slightlymagic.maxmtg.Predicate;

import forge.deck.Deck;
import forge.game.GameType;
import forge.item.CardPrinted;
import forge.item.InventoryItem;
import forge.item.ItemPool;
import forge.item.ItemPoolView;

public abstract class DeckEditorBase extends JFrame implements DeckDisplay  {
    private static final long serialVersionUID = -401223933343539977L;

    protected FilterCheckBoxes filterBoxes;
    // set this to false when resetting filter from code (like "clearFiltersPressed"), reset when done.
    protected boolean isFiltersChangeFiringUpdate = true;
 

    protected CardPanelBase cardView;
    
    // CardPools and Table data for top and bottom lists
    protected TableWithCards top;
    protected TableWithCards bottom;

    
    private GameType gameType;
    public GameType getGameType() { return gameType; }
    
    // top shows available card pool
    // if constructed, top shows all cards
    // if sealed, top shows N booster packs
    // if draft, top shows cards that were chosen
    public final TableWithCards getTopTableModel() { return top; }
    public final ItemPoolView<InventoryItem> getTop() { return top.getCards(); }
    // bottom shows player's choice - be it deck or draft
    public final ItemPoolView<InventoryItem> getBottom() { return bottom.getCards(); }

    // THIS IS HERE FOR OVERLOADING!!!1
    // or may be return abstract getFilter from derived class + this filter ... virtual protected member, but later
    protected abstract Predicate<InventoryItem> buildFilter();

    void analysisButton_actionPerformed(ActionEvent e) {
        ItemPoolView<CardPrinted> deck = ItemPool.createFrom(bottom.getCards(), CardPrinted.class); 
        if (deck.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Cards in deck not found.", "Analysis Deck",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            DeckEditorBase g = DeckEditorBase.this;
            DeckAnalysis dAnalysis = new DeckAnalysis(g, deck);
            dAnalysis.setVisible(true);
            g.setEnabled(false);
        }
    }

    public DeckEditorBase(GameType gametype)
    {
        gameType = gametype;
    }

    public void setDeck(ItemPoolView<CardPrinted> topParam, ItemPoolView<CardPrinted> bottomParam, GameType gt) {
        gameType = gt;
        top.setDeck(topParam);
        bottom.setDeck(bottomParam);
    }

    public <T extends InventoryItem> void setItems(ItemPoolView<T> topParam, ItemPoolView<T> bottomParam, GameType gt) {
        gameType = gt;
        top.setDeck(topParam);
        bottom.setDeck(bottomParam);
    }

    public void updateDisplay() {
        top.setFilter(buildFilter());
    }

    protected ItemListener itemListenerUpdatesDisplay = new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
            if (isFiltersChangeFiringUpdate) { updateDisplay(); }
        }
    };

    /** This class is used for a feature: when you start typing card name, the list gets auto-filtered. */
    protected class OnChangeTextUpdateDisplay implements DocumentListener {
        private void onChange() { if (isFiltersChangeFiringUpdate) { updateDisplay(); } }
        @Override public void insertUpdate(DocumentEvent e) { onChange(); }
        @Override public void removeUpdate(DocumentEvent e) { onChange(); }
        @Override public void changedUpdate(DocumentEvent e) { } // Happend only on ENTER pressed
    }
    
    public Deck getDeck() {
        Deck deck = new Deck(gameType);
        deck.addMain(ItemPool.createFrom(getBottom(), CardPrinted.class));

        //if sealed or draft, move "top" to sideboard
        if (gameType.isLimited() && gameType != GameType.Quest) {
            deck.addSideboard(ItemPool.createFrom(getTop(), CardPrinted.class));
        }
        return deck;
    }//getDeck()
    
}
