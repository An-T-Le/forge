package forge.card.abilityfactory.ai;

import java.util.ArrayList;
import java.util.List;

import forge.Card;
import forge.CardLists;
import forge.CardPredicates;
import forge.Singletons;
import forge.card.abilityfactory.SpellAiLogic;
import forge.card.cost.Cost;
import forge.card.spellability.SpellAbility;
import forge.game.ai.ComputerUtil;
import forge.game.ai.ComputerUtilCombat;
import forge.game.ai.ComputerUtilCost;
import forge.game.phase.PhaseType;
import forge.game.player.AIPlayer;
import forge.game.zone.ZoneType;

public class RegenerateAllAi extends SpellAiLogic {

    @Override
    protected boolean canPlayAI(AIPlayer ai, SpellAbility sa) {
        final Card hostCard = sa.getSourceCard();
        boolean chance = false;
        final Cost abCost = sa.getPayCosts();
        if (abCost != null) {
            // AI currently disabled for these costs
            if (!ComputerUtilCost.checkSacrificeCost(ai, abCost, hostCard)) {
                return false;
            }

            if (!ComputerUtilCost.checkCreatureSacrificeCost(ai, abCost, hostCard)) {
                return false;
            }

            if (!ComputerUtilCost.checkLifeCost(ai, abCost, hostCard, 4, null)) {
                return false;
            }
        }

        // filter AIs battlefield by what I can target
        String valid = "";

        if (sa.hasParam("ValidCards")) {
            valid = sa.getParam("ValidCards");
        }

        List<Card> list = Singletons.getModel().getGame().getCardsIn(ZoneType.Battlefield);
        list = CardLists.getValidCards(list, valid.split(","), hostCard.getController(), hostCard);
        list = CardLists.filter(list, CardPredicates.isController(ai));

        if (list.size() == 0) {
            return false;
        }

        int numSaved = 0;
        if (Singletons.getModel().getGame().getStack().size() > 0) {
            final ArrayList<Object> objects = ComputerUtil.predictThreatenedObjects(sa.getActivatingPlayer(), sa);

            for (final Card c : list) {
                if (objects.contains(c) && c.getShield() == 0) {
                    numSaved++;
                }
            }
        } else {
            if (Singletons.getModel().getGame().getPhaseHandler().is(PhaseType.COMBAT_DECLARE_BLOCKERS_INSTANT_ABILITY)) {
                final List<Card> combatants = CardLists.filter(list, CardPredicates.Presets.CREATURES);

                for (final Card c : combatants) {
                    if (c.getShield() == 0 && ComputerUtilCombat.combatantWouldBeDestroyed(ai, c)) {
                        numSaved++;
                    }
                }
            }
        }

        if (numSaved > 1) {
            chance = true;
        }

        return chance;
    }

    @Override
    protected boolean doTriggerAINoCost(AIPlayer aiPlayer, SpellAbility sa, boolean mandatory) {
        boolean chance = true;

        return chance;
    }

}
