package forge.game.ability.effects;

import java.util.Iterator;
import java.util.List;

import forge.Command;
import forge.game.Game;
import forge.game.ability.AbilityFactory;
import forge.game.ability.AbilityUtils;
import forge.game.ability.SpellAbilityEffect;
import forge.game.card.Card;
import forge.game.card.CardShields;
import forge.game.player.Player;
import forge.game.spellability.SpellAbility;
import forge.game.spellability.TargetRestrictions;

public class RegenerateEffect extends SpellAbilityEffect {

    /* (non-Javadoc)
     * @see forge.card.abilityfactory.SpellEffect#getStackDescription(java.util.Map, forge.card.spellability.SpellAbility)
     */
    @Override
    protected String getStackDescription(SpellAbility sa) {
        final StringBuilder sb = new StringBuilder();
        final List<Card> tgtCards = getTargetCards(sa);

        if (tgtCards.size() > 0) {
            sb.append("Regenerate ");

            final Iterator<Card> it = tgtCards.iterator();
            while (it.hasNext()) {
                final Card tgtC = it.next();
                if (tgtC.isFaceDown()) {
                    sb.append("Morph");
                } else {
                    sb.append(tgtC);
                }

                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        sb.append(".");

        return sb.toString();
    }

    @Override
    public void resolve(SpellAbility sa) {
        final TargetRestrictions tgt = sa.getTargetRestrictions();
        final Game game = sa.getActivatingPlayer().getGame();
        final Card sourceCard = sa.getSourceCard();

        for (final Card tgtC : getTargetCards(sa)) {
            final Command untilEOT = new Command() {
                private static final long serialVersionUID = 1922050611313909200L;

                @Override
                public void run() {
                    tgtC.resetShield();
                }
            };

            if (tgtC.isInPlay() && (tgt == null || tgt.canTgtPlayer() || tgtC.canBeTargetedBy(sa))) {
            	SpellAbility triggerSA = null;
            	if (sa.hasParam("RegenerationTrigger")) {
            		String abString = sa.getSourceCard().getSVar(sa.getParam("RegenerationTrigger"));
            		if (sa.hasParam("ReplacePlayerName")) { // Soldevi Sentry
            			String def = sa.getParam("ReplacePlayerName");
            			List<Player> replaced = AbilityUtils.getDefinedPlayers(sourceCard, def, sa);
            			abString = abString.replace(def, replaced.isEmpty() ? "" : replaced.get(0).getName());
            		} else if (sa.hasParam("ReplaceCardUID")) { // Debt of Loyalty
            			String def = sa.getParam("ReplaceCardUID");
            			List<Card> replaced = AbilityUtils.getDefinedCards(sourceCard, def, sa);
            			abString = abString.replace(def, replaced.isEmpty() ? "" : Integer.toString(replaced.get(0).getUniqueNumber()));
            		}
            		triggerSA = AbilityFactory.getAbility(abString, sourceCard);
            		triggerSA.setActivatingPlayer(sa.getActivatingPlayer());
            		triggerSA.setTrigger(true);
            		triggerSA.setSourceCard(sourceCard);
            	}
            	CardShields shield = new CardShields(sa, triggerSA);
                tgtC.addShield(shield);
                game.getEndOfTurn().addUntil(untilEOT);
            }
        }
    } // regenerateResolve

}