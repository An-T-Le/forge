package forge.card.ability.ai;


import forge.card.ability.SpellAbilityAi;
import forge.card.spellability.SpellAbility;
import forge.game.player.Player;

public class SetStateAi extends SpellAbilityAi {
    @Override
    protected boolean canPlayAI(Player aiPlayer, SpellAbility sa) {
        if (sa.getTargetRestrictions() == null && "Transform".equals(sa.getParam("Mode"))) {
            return true;
        }
        return false;
    }

    @Override
    public boolean chkAIDrawback(SpellAbility sa, Player aiPlayer) {
        // Gross generalization, but this always considers alternate
        // states more powerful
        return !sa.getSourceCard().isInAlternateState();
    }


    /* (non-Javadoc)
    * @see forge.card.abilityfactory.SpellAiLogic#doTriggerAINoCost(forge.game.player.Player, java.util.Map, forge.card.spellability.SpellAbility, boolean)
    */
    @Override
    protected boolean doTriggerAINoCost(Player aiPlayer, SpellAbility sa, boolean mandatory) {
        return true;
    }
}
