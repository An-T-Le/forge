package forge.card.abilityFactory;

import java.util.ArrayList;
import java.util.HashMap;

import forge.AllZone;
import forge.AllZoneUtil;
import forge.Card;
import forge.CardList;
import forge.CardUtil;
import forge.Command;
import forge.ComputerUtil;
import forge.Constant;
import forge.Constant.Zone;
import forge.GameActionUtil;
import forge.Player;
import forge.card.cardFactory.CardFactoryUtil;
import forge.card.cost.Cost;
import forge.card.spellability.Ability;
import forge.card.spellability.Ability_Sub;
import forge.card.spellability.SpellAbility;
import forge.card.spellability.SpellAbility_Condition;
import forge.card.spellability.SpellAbility_Restriction;
import forge.card.spellability.Spell_Permanent;
import forge.card.spellability.Target;

/**
 * <p>
 * AbilityFactory class.
 * </p>
 * 
 * @author Forge
 * @version $Id$
 */
public class AbilityFactory {

    private Card hostC = null;

    /**
     * <p>
     * getHostCard.
     * </p>
     * 
     * @return a {@link forge.Card} object.
     */
    public final Card getHostCard() {
        return hostC;
    }

    private HashMap<String, String> mapParams = new HashMap<String, String>();

    /**
     * <p>
     * Getter for the field <code>mapParams</code>.
     * </p>
     * 
     * @return a {@link java.util.HashMap} object.
     */
    public final HashMap<String, String> getMapParams() {
        return mapParams;
    }

    private boolean isAb = false;
    private boolean isSp = false;
    private boolean isDb = false;

    /**
     * <p>
     * isAbility.
     * </p>
     * 
     * @return a boolean.
     */
    public final boolean isAbility() {
        return isAb;
    }

    /**
     * <p>
     * isSpell.
     * </p>
     * 
     * @return a boolean.
     */
    public final boolean isSpell() {
        return isSp;
    }

    /**
     * <p>
     * isDrawback.
     * </p>
     * 
     * @return a boolean.
     */
    public final boolean isDrawback() {
        return isDb;
    }

    private Cost abCost = null;

    /**
     * <p>
     * Getter for the field <code>abCost</code>.
     * </p>
     * 
     * @return a {@link forge.card.cost.Cost} object.
     */
    public final Cost getAbCost() {
        return abCost;
    }

    private boolean isTargeted = false;
    private boolean hasValid = false;
    private Target abTgt = null;

    /**
     * <p>
     * isTargeted.
     * </p>
     * 
     * @return a boolean.
     */
    public final boolean isTargeted() {
        return isTargeted;
    }

    /**
     * <p>
     * hasValid.
     * </p>
     * 
     * @return a boolean.
     */
    public final boolean hasValid() {
        return hasValid;
    }

    /**
     * <p>
     * Getter for the field <code>abTgt</code>.
     * </p>
     * 
     * @return a {@link forge.card.spellability.Target} object.
     */
    public final Target getAbTgt() {
        return abTgt;
    }

    /**
     * <p>
     * isCurse.
     * </p>
     * 
     * @return a boolean.
     */
    public final boolean isCurse() {
        return mapParams.containsKey("IsCurse");
    }

    private boolean hasSubAb = false;

    /**
     * <p>
     * hasSubAbility.
     * </p>
     * 
     * @return a boolean.
     */
    public final boolean hasSubAbility() {
        return hasSubAb;
    }

    private boolean hasSpDesc = false;

    /**
     * <p>
     * hasSpDescription.
     * </p>
     * 
     * @return a boolean.
     */
    public final boolean hasSpDescription() {
        return hasSpDesc;
    }

    private String api = "";

    /**
     * <p>
     * getAPI.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public final String getAPI() {
        return api;
    }

    // *******************************************************

    /**
     * <p>
     * Getter for the field <code>mapParams</code>.
     * </p>
     * 
     * @param abString
     *            a {@link java.lang.String} object.
     * @param hostCard
     *            a {@link forge.Card} object.
     * @return a {@link java.util.HashMap} object.
     */
    public final HashMap<String, String> getMapParams(final String abString, final Card hostCard) {
        HashMap<String, String> mapParameters = new HashMap<String, String>();

        if (!(abString.length() > 0)) {
            throw new RuntimeException("AbilityFactory : getAbility -- abString too short in " + hostCard.getName()
                    + ": [" + abString + "]");
        }

        String[] a = abString.split("\\|");

        for (int aCnt = 0; aCnt < a.length; aCnt++) {
            a[aCnt] = a[aCnt].trim();
        }

        if (!(a.length > 0)) {
            throw new RuntimeException("AbilityFactory : getAbility -- a[] too short in " + hostCard.getName());
        }

        for (int i = 0; i < a.length; i++) {
            String[] aa = a[i].split("\\$");

            for (int aaCnt = 0; aaCnt < aa.length; aaCnt++) {
                aa[aaCnt] = aa[aaCnt].trim();
            }

            if (aa.length != 2) {
                StringBuilder sb = new StringBuilder();
                sb.append("AbilityFactory Parsing Error in getAbility() : Split length of ");
                sb.append(a[i]).append(" in ").append(hostCard.getName()).append(" is not 2.");
                throw new RuntimeException(sb.toString());
            }

            mapParameters.put(aa[0], aa[1]);
        }

        return mapParameters;
    }

    /**
     * <p>
     * getAbility.
     * </p>
     * 
     * @param abString
     *            a {@link java.lang.String} object.
     * @param hostCard
     *            a {@link forge.Card} object.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public final SpellAbility getAbility(final String abString, final Card hostCard) {

        SpellAbility spellAbility = null;

        hostC = hostCard;

        mapParams = getMapParams(abString, hostCard);

        // parse universal parameters

        if (mapParams.containsKey("AB")) {
            isAb = true;
            api = mapParams.get("AB");
        } else if (mapParams.containsKey("SP")) {
            isSp = true;
            api = mapParams.get("SP");
        } else if (mapParams.containsKey("DB")) {
            isDb = true;
            api = mapParams.get("DB");
        } else {
            throw new RuntimeException("AbilityFactory : getAbility -- no API in " + hostCard.getName());
        }

        if (!isDb) {
            if (!mapParams.containsKey("Cost")) {
                throw new RuntimeException("AbilityFactory : getAbility -- no Cost in " + hostCard.getName());
            }
            abCost = new Cost(mapParams.get("Cost"), hostCard.getName(), isAb);
        }

        if (mapParams.containsKey("ValidTgts")) {
            hasValid = true;
            isTargeted = true;
        }

        if (mapParams.containsKey("Tgt")) {
            isTargeted = true;
        }

        if (isTargeted) {
            String min = mapParams.containsKey("TargetMin") ? mapParams.get("TargetMin") : "1";
            String max = mapParams.containsKey("TargetMax") ? mapParams.get("TargetMax") : "1";

            if (hasValid) {
                // TgtPrompt now optional
                StringBuilder sb = new StringBuilder();
                if (hostC != null) {
                    sb.append(hostC + " - ");
                }
                String prompt = mapParams.containsKey("TgtPrompt") ? mapParams.get("TgtPrompt") : "Select target "
                        + mapParams.get("ValidTgts");
                sb.append(prompt);
                abTgt = new Target(hostC, sb.toString(), mapParams.get("ValidTgts").split(","), min, max);
            } else {
                abTgt = new Target(hostC, mapParams.get("Tgt"), min, max);
            }

            if (mapParams.containsKey("TgtZone")) { // if Targeting something
                                                    // not in play, this Key
                                                    // should be set
                abTgt.setZone(Zone.listValueOf(mapParams.get("TgtZone")));
            }

            // Target Type mostly for Counter: Spell,Activated,Triggered,Ability
            // (or any combination of)
            // Ability = both activated and triggered abilities
            if (mapParams.containsKey("TargetType")) {
                abTgt.setTargetSpellAbilityType(mapParams.get("TargetType"));
            }

            // TargetValidTargeting most for Counter: e.g. target spell that
            // targets X.
            if (mapParams.containsKey("TargetValidTargeting")) {
                abTgt.setSAValidTargeting(mapParams.get("TargetValidTargeting"));
            }

            if (mapParams.containsKey("TargetUnique")) {
                abTgt.setUniqueTargets(true);
            }
        }

        hasSubAb = mapParams.containsKey("SubAbility");

        hasSpDesc = mapParams.containsKey("SpellDescription");

        // ***********************************
        // Match API keywords

        if (api.equals("DealDamage")) {
            AbilityFactory_DealDamage dd = new AbilityFactory_DealDamage(this);

            if (isAb) {
                spellAbility = dd.getAbility();
            } else if (isSp) {
                spellAbility = dd.getSpell();
            } else if (isDb) {
                spellAbility = dd.getDrawback();
            }
        }

        else if (api.equals("DamageAll")) {
            AbilityFactory_DealDamage dd = new AbilityFactory_DealDamage(this);
            if (isAb) {
                spellAbility = dd.getAbilityDamageAll();
            } else if (isSp) {
                spellAbility = dd.getSpellDamageAll();
            } else if (isDb) {
                spellAbility = dd.getDrawbackDamageAll();
            }
        }

        else if (api.equals("PutCounter")) {
            if (isAb) {
                spellAbility = AbilityFactory_Counters.createAbilityPutCounters(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Counters.createSpellPutCounters(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Counters.createDrawbackPutCounters(this);
            }
        }

        else if (api.equals("PutCounterAll")) {
            if (isAb) {
                spellAbility = AbilityFactory_Counters.createAbilityPutCounterAll(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Counters.createSpellPutCounterAll(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Counters.createDrawbackPutCounterAll(this);
            }
        }

        else if (api.equals("RemoveCounter")) {
            if (isAb) {
                spellAbility = AbilityFactory_Counters.createAbilityRemoveCounters(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Counters.createSpellRemoveCounters(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Counters.createDrawbackRemoveCounters(this);
            }
        }

        else if (api.equals("RemoveCounterAll")) {
            if (isAb) {
                spellAbility = AbilityFactory_Counters.createAbilityRemoveCounterAll(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Counters.createSpellRemoveCounterAll(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Counters.createDrawbackRemoveCounterAll(this);
            }
        }

        else if (api.equals("Proliferate")) {
            if (isAb) {
                spellAbility = AbilityFactory_Counters.createAbilityProliferate(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Counters.createSpellProliferate(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Counters.createDrawbackProliferate(this);
            }
        }

        else if (api.equals("MoveCounter")) {
            if (isAb) {
                spellAbility = AbilityFactory_Counters.createAbilityMoveCounters(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Counters.createSpellMoveCounters(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Counters.createDrawbackMoveCounters(this);
            }
        }

        else if (api.equals("ChangeZone")) {
            if (isAb) {
                spellAbility = AbilityFactory_ChangeZone.createAbilityChangeZone(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_ChangeZone.createSpellChangeZone(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_ChangeZone.createDrawbackChangeZone(this);
            }
        }

        else if (api.equals("ChangeZoneAll")) {
            if (isAb) {
                spellAbility = AbilityFactory_ChangeZone.createAbilityChangeZoneAll(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_ChangeZone.createSpellChangeZoneAll(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_ChangeZone.createDrawbackChangeZoneAll(this);
            }
        }

        else if (api.equals("Pump")) {
            AbilityFactory_Pump afPump = new AbilityFactory_Pump(this);

            if (isAb) {
                spellAbility = afPump.getAbilityPump();
            } else if (isSp) {
                spellAbility = afPump.getSpellPump();
            } else if (isDb) {
                spellAbility = afPump.getDrawbackPump();
            }

            if (isAb || isSp) {
                hostCard.setSVar("PlayMain1", "TRUE");
            }
        }

        else if (api.equals("PumpAll")) {
            AbilityFactory_Pump afPump = new AbilityFactory_Pump(this);

            if (isAb) {
                spellAbility = afPump.getAbilityPumpAll();
            } else if (isSp) {
                spellAbility = afPump.getSpellPumpAll();
            } else if (isDb) {
                spellAbility = afPump.getDrawbackPumpAll();
            }

            if (isAb || isSp) {
                hostCard.setSVar("PlayMain1", "TRUE");
            }
        }

        else if (api.equals("GainLife")) {
            if (isAb) {
                spellAbility = AbilityFactory_AlterLife.createAbilityGainLife(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_AlterLife.createSpellGainLife(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_AlterLife.createDrawbackGainLife(this);
            }
        }

        else if (api.equals("LoseLife")) {
            if (isAb) {
                spellAbility = AbilityFactory_AlterLife.createAbilityLoseLife(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_AlterLife.createSpellLoseLife(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_AlterLife.createDrawbackLoseLife(this);
            }
        }

        else if (api.equals("SetLife")) {
            if (isAb) {
                spellAbility = AbilityFactory_AlterLife.createAbilitySetLife(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_AlterLife.createSpellSetLife(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_AlterLife.createDrawbackSetLife(this);
            }
        }

        else if (api.equals("ExchangeLife")) {
            if (isAb) {
                spellAbility = AbilityFactory_AlterLife.createAbilityExchangeLife(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_AlterLife.createSpellExchangeLife(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_AlterLife.createDrawbackExchangeLife(this);
            }
        }

        else if (api.equals("Poison")) {
            if (isAb) {
                spellAbility = AbilityFactory_AlterLife.createAbilityPoison(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_AlterLife.createSpellPoison(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_AlterLife.createDrawbackPoison(this);
            }
        }

        else if (api.equals("Fog")) {
            if (isAb) {
                spellAbility = AbilityFactory_Combat.createAbilityFog(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Combat.createSpellFog(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Combat.createDrawbackFog(this);
            }
        }

        else if (api.equals("RemoveFromCombat")) {
            if (isAb) {
                spellAbility = AbilityFactory_Combat.createAbilityRemoveFromCombat(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Combat.createSpellRemoveFromCombat(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Combat.createDrawbackRemoveFromCombat(this);
            }
        }

        else if (api.equals("Untap")) {
            if (isAb) {
                spellAbility = AbilityFactory_PermanentState.createAbilityUntap(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_PermanentState.createSpellUntap(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_PermanentState.createDrawbackUntap(this);
            }
        }

        else if (api.equals("UntapAll")) {
            if (isAb) {
                spellAbility = AbilityFactory_PermanentState.createAbilityUntapAll(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_PermanentState.createSpellUntapAll(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_PermanentState.createDrawbackUntapAll(this);
            }
        }

        else if (api.equals("Tap")) {
            if (isAb) {
                spellAbility = AbilityFactory_PermanentState.createAbilityTap(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_PermanentState.createSpellTap(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_PermanentState.createDrawbackTap(this);
            }
        }

        else if (api.equals("TapAll")) {
            if (isAb) {
                spellAbility = AbilityFactory_PermanentState.createAbilityTapAll(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_PermanentState.createSpellTapAll(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_PermanentState.createDrawbackTapAll(this);
            }
        }

        else if (api.equals("TapOrUntap")) {
            if (isAb) {
                spellAbility = AbilityFactory_PermanentState.createAbilityTapOrUntap(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_PermanentState.createSpellTapOrUntap(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_PermanentState.createDrawbackTapOrUntap(this);
            }
        }

        else if (api.equals("Phases")) {
            if (isAb) {
                spellAbility = AbilityFactory_PermanentState.createAbilityPhases(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_PermanentState.createSpellPhases(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_PermanentState.createDrawbackPhases(this);
            }
        }

        else if (api.equals("PreventDamage")) {
            if (isAb) {
                spellAbility = AbilityFactory_PreventDamage.getAbilityPreventDamage(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_PreventDamage.getSpellPreventDamage(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_PreventDamage.createDrawbackPreventDamage(this);
            }
        }

        else if (api.equals("Regenerate")) {
            if (isAb) {
                spellAbility = AbilityFactory_Regenerate.getAbilityRegenerate(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Regenerate.getSpellRegenerate(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Regenerate.createDrawbackRegenerate(this);
            }
        }

        else if (api.equals("Draw")) {
            if (isAb) {
                spellAbility = AbilityFactory_ZoneAffecting.createAbilityDraw(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_ZoneAffecting.createSpellDraw(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_ZoneAffecting.createDrawbackDraw(this);
            }
        }

        else if (api.equals("Mill")) {
            if (isAb) {
                spellAbility = AbilityFactory_ZoneAffecting.createAbilityMill(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_ZoneAffecting.createSpellMill(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_ZoneAffecting.createDrawbackMill(this);
            }
        }

        else if (api.equals("Scry")) {
            if (isAb) {
                spellAbility = AbilityFactory_Reveal.createAbilityScry(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Reveal.createSpellScry(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Reveal.createDrawbackScry(this);
            }
        }

        else if (api.equals("RearrangeTopOfLibrary")) {
            if (isAb) {
                spellAbility = AbilityFactory_Reveal.createRearrangeTopOfLibraryAbility(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Reveal.createRearrangeTopOfLibrarySpell(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Reveal.createRearrangeTopOfLibraryDrawback(this);
            }
        }

        else if (api.equals("Sacrifice")) {
            if (isAb) {
                spellAbility = AbilityFactory_Sacrifice.createAbilitySacrifice(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Sacrifice.createSpellSacrifice(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Sacrifice.createDrawbackSacrifice(this);
            }
        }

        else if (api.equals("SacrificeAll")) {
            if (isAb) {
                spellAbility = AbilityFactory_Sacrifice.createAbilitySacrificeAll(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Sacrifice.createSpellSacrificeAll(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Sacrifice.createDrawbackSacrificeAll(this);
            }
        }

        else if (api.equals("Destroy")) {
            if (isAb) {
                spellAbility = AbilityFactory_Destroy.createAbilityDestroy(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Destroy.createSpellDestroy(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Destroy.createDrawbackDestroy(this);
            }
        }

        else if (api.equals("DestroyAll")) {
            if (isAb) {
                spellAbility = AbilityFactory_Destroy.createAbilityDestroyAll(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Destroy.createSpellDestroyAll(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Destroy.createDrawbackDestroyAll(this);
            }
        }

        else if (api.equals("Mana")) {
            String produced = mapParams.get("Produced");
            if (isAb) {
                spellAbility = AbilityFactory_Mana.createAbilityMana(this, produced);
            }
            if (isSp) {
                spellAbility = AbilityFactory_Mana.createSpellMana(this, produced);
            }
            if (isDb) {
                spellAbility = AbilityFactory_Mana.createDrawbackMana(this, produced);
            }
        }

        else if (api.equals("ManaReflected")) {
            // Reflected mana will have a filler for produced of "1"
            if (isAb) {
                spellAbility = AbilityFactory_Mana.createAbilityManaReflected(this, "1");
            }
            if (isSp) { // shouldn't really happen i think?
                spellAbility = AbilityFactory_Mana.createSpellManaReflected(this, "1");
            }
        }

        else if (api.equals("Token")) {
            AbilityFactory_Token aft = new AbilityFactory_Token(this);

            if (isAb) {
                spellAbility = aft.getAbility();
            } else if (isSp) {
                spellAbility = aft.getSpell();
            } else if (isDb) {
                spellAbility = aft.getDrawback();
            }
        }

        else if (api.equals("GainControl")) {
            AbilityFactory_GainControl afControl = new AbilityFactory_GainControl(this);

            if (isAb) {
                spellAbility = afControl.getAbilityGainControl();
            } else if (isSp) {
                spellAbility = afControl.getSpellGainControl();
            } else if (isDb) {
                spellAbility = afControl.getDrawbackGainControl();
            }
        }

        else if (api.equals("Discard")) {
            if (isAb) {
                spellAbility = AbilityFactory_ZoneAffecting.createAbilityDiscard(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_ZoneAffecting.createSpellDiscard(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_ZoneAffecting.createDrawbackDiscard(this);
            }
        }

        else if (api.equals("Counter")) {
            AbilityFactory_CounterMagic c = new AbilityFactory_CounterMagic(this);

            if (isTargeted) { // Since all "Counter" ABs Counter things on the
                              // Stack no need for it to be everywhere
                abTgt.setZone(Zone.Stack);
            }

            if (isAb) {
                spellAbility = c.getAbilityCounter(this);
            } else if (isSp) {
                spellAbility = c.getSpellCounter(this);
            } else if (isDb) {
                spellAbility = c.getDrawbackCounter(this);
            }
        }

        else if (api.equals("AddTurn")) {
            if (isAb) {
                spellAbility = AbilityFactory_Turns.createAbilityAddTurn(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Turns.createSpellAddTurn(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Turns.createDrawbackAddTurn(this);
            }
        }

        else if (api.equals("Clash")) {
            if (isAb) {
                spellAbility = AbilityFactory_Clash.getAbilityClash(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Clash.getSpellClash(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Clash.getDrawbackClash(this);
            }
        }

        else if (api.equals("Animate")) {
            if (isAb) {
                spellAbility = AbilityFactory_Animate.createAbilityAnimate(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Animate.createSpellAnimate(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Animate.createDrawbackAnimate(this);
            }
        }

        else if (api.equals("Effect")) {
            if (isAb) {
                spellAbility = AbilityFactory_Effect.createAbilityEffect(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Effect.createSpellEffect(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Effect.createDrawbackEffect(this);
            }
        }

        else if (api.equals("WinsGame")) {
            if (isAb) {
                spellAbility = AbilityFactory_EndGameCondition.createAbilityWinsGame(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_EndGameCondition.createSpellWinsGame(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_EndGameCondition.createDrawbackWinsGame(this);
            }
        }

        else if (api.equals("LosesGame")) {
            if (isAb) {
                spellAbility = AbilityFactory_EndGameCondition.createAbilityLosesGame(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_EndGameCondition.createSpellLosesGame(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_EndGameCondition.createDrawbackLosesGame(this);
            }
        }

        else if (api.equals("RevealHand")) {
            if (isAb) {
                spellAbility = AbilityFactory_Reveal.createAbilityRevealHand(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Reveal.createSpellRevealHand(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Reveal.createDrawbackRevealHand(this);
            }
        }

        else if (api.equals("Reveal")) {
            if (isAb) {
                spellAbility = AbilityFactory_Reveal.createAbilityReveal(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Reveal.createSpellReveal(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Reveal.createDrawbackReveal(this);
            }
        }

        else if (api.equals("Dig")) {
            if (isAb) {
                spellAbility = AbilityFactory_Reveal.createAbilityDig(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Reveal.createSpellDig(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Reveal.createDrawbackDig(this);
            }
        }

        else if (api.equals("DigUntil")) {
            if (isAb) {
                spellAbility = AbilityFactory_Reveal.createAbilityDigUntil(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Reveal.createSpellDigUntil(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Reveal.createDrawbackDigUntil(this);
            }
        }

        else if (api.equals("Shuffle")) {
            if (isAb) {
                spellAbility = AbilityFactory_ZoneAffecting.createAbilityShuffle(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_ZoneAffecting.createSpellShuffle(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_ZoneAffecting.createDrawbackShuffle(this);
            }
        }

        else if (api.equals("ChooseType")) {
            if (isAb) {
                spellAbility = AbilityFactory_Choose.createAbilityChooseType(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Choose.createSpellChooseType(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Choose.createDrawbackChooseType(this);
            }
        }

        else if (api.equals("ChooseColor")) {
            if (isAb) {
                spellAbility = AbilityFactory_Choose.createAbilityChooseColor(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Choose.createSpellChooseColor(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Choose.createDrawbackChooseColor(this);
            }
        }

        else if (api.equals("ChooseNumber")) {
            if (isAb) {
                spellAbility = AbilityFactory_Choose.createAbilityChooseNumber(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Choose.createSpellChooseNumber(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Choose.createDrawbackChooseNumber(this);
            }
        }

        else if (api.equals("ChoosePlayer")) {
            if (isAb) {
                spellAbility = AbilityFactory_Choose.createAbilityChoosePlayer(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Choose.createSpellChoosePlayer(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Choose.createDrawbackChoosePlayer(this);
            }
        }

        else if (api.equals("NameCard")) {
            if (isAb) {
                spellAbility = AbilityFactory_Choose.createAbilityNameCard(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Choose.createSpellNameCard(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Choose.createDrawbackNameCard(this);
            }
        }

        else if (api.equals("CopyPermanent")) {
            if (isAb) {
                spellAbility = AbilityFactory_Copy.createAbilityCopyPermanent(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Copy.createSpellCopyPermanent(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Copy.createDrawbackCopyPermanent(this);
            }
        }

        else if (api.equals("CopySpell")) {
            if (isTargeted) { // Since all "CopySpell" ABs copy things on the
                              // Stack no need for it to be everywhere
                abTgt.setZone(Zone.Stack);
            }

            if (isAb) {
                spellAbility = AbilityFactory_Copy.createAbilityCopySpell(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Copy.createSpellCopySpell(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Copy.createDrawbackCopySpell(this);
            }

            hostCard.setCopiesSpells(true);
        }

        else if (api.equals("FlipACoin")) {
            if (isAb) {
                spellAbility = AbilityFactory_Clash.createAbilityFlip(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Clash.createSpellFlip(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Clash.createDrawbackFlip(this);
            }
        }

        else if (api.equals("DelayedTrigger")) {
            if (isAb) {
                spellAbility = AbilityFactory_DelayedTrigger.getAbility(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_DelayedTrigger.getSpell(this);
            }
            if (isDb) {
                spellAbility = AbilityFactory_DelayedTrigger.getDrawback(this);
            }
        }

        else if (api.equals("Cleanup")) {
            if (isDb) {
                spellAbility = AbilityFactory_Cleanup.getDrawback(this);
            }
        }

        else if (api.equals("RegenerateAll")) {
            if (isAb) {
                spellAbility = AbilityFactory_Regenerate.getAbilityRegenerateAll(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Regenerate.getSpellRegenerateAll(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Regenerate.createDrawbackRegenerateAll(this);
            }
        }

        else if (api.equals("AnimateAll")) {
            if (isAb) {
                spellAbility = AbilityFactory_Animate.createAbilityAnimateAll(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Animate.createSpellAnimateAll(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Animate.createDrawbackAnimateAll(this);
            }
        }

        else if (api.equals("Debuff")) {
            if (isAb) {
                spellAbility = AbilityFactory_Debuff.createAbilityDebuff(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Debuff.createSpellDebuff(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Debuff.createDrawbackDebuff(this);
            }
        }

        else if (api.equals("DebuffAll")) {
            if (isAb) {
                spellAbility = AbilityFactory_Debuff.createAbilityDebuffAll(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Debuff.createSpellDebuffAll(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Debuff.createDrawbackDebuffAll(this);
            }
        }

        else if (api.equals("DrainMana")) {
            if (isAb) {
                spellAbility = AbilityFactory_Mana.createAbilityDrainMana(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Mana.createSpellDrainMana(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Mana.createDrawbackDrainMana(this);
            }
        }

        else if (api.equals("Protection")) {
            if (isAb) {
                spellAbility = AbilityFactory_Protection.createAbilityProtection(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Protection.createSpellProtection(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Protection.createDrawbackProtection(this);
            }
        }

        else if (api.equals("Attach")) {
            if (isAb) {
                spellAbility = AbilityFactory_Attach.createAbilityAttach(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Attach.createSpellAttach(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Attach.createDrawbackAttach(this);
            }
        }

        else if (api.equals("ProtectionAll")) {
            if (isAb) {
                spellAbility = AbilityFactory_Protection.createAbilityProtectionAll(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Protection.createSpellProtectionAll(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Protection.createDrawbackProtectionAll(this);
            }
        }

        else if (api.equals("MustAttack")) {
            if (isAb) {
                spellAbility = AbilityFactory_Combat.createAbilityMustAttack(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Combat.createSpellMustAttack(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Combat.createDrawbackMustAttack(this);
            }
        }

        else if (api.equals("MustBlock")) {
            if (isAb) {
                spellAbility = AbilityFactory_Combat.createAbilityMustBlock(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Combat.createSpellMustBlock(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_Combat.createDrawbackMustBlock(this);
            }
        }

        else if (api.equals("Charm")) {
            if (isAb) {
                spellAbility = AbilityFactory_Charm.createAbilityCharm(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_Charm.createSpellCharm(this);
            }
            spellAbility.setIsCharm(true);
            int num = Integer.parseInt(mapParams.containsKey("CharmNum") ? mapParams.get("CharmNum") : "1");
            spellAbility.setCharmNumber(num);
            int min = mapParams.containsKey("MinCharmNum") ? Integer.parseInt(mapParams.get("MinCharmNum")) : num;
            spellAbility.setMinCharmNumber(min);

            String[] saChoices = mapParams.get("Choices").split(",");
            for (int i = 0; i < saChoices.length; i++) {
                String ab = hostC.getSVar(saChoices[i]);
                AbilityFactory charmAF = new AbilityFactory();
                spellAbility.addCharmChoice(charmAF.getAbility(ab, hostC));
            }
        }

        else if (api.equals("ChangeState")) {
            if (isAb) {
                spellAbility = AbilityFactory_ChangeState.getChangeStateAbility(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_ChangeState.getChangeStateSpell(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_ChangeState.getChangeStateDrawback(this);
            }
        }

        else if (api.equals("ChangeStateAll")) {
            if (isAb) {
                spellAbility = AbilityFactory_ChangeState.getChangeStateAllAbility(this);
            } else if (isSp) {
                spellAbility = AbilityFactory_ChangeState.getChangeStateAllSpell(this);
            } else if (isDb) {
                spellAbility = AbilityFactory_ChangeState.getChangeStateAllDrawback(this);
            }
        }

        if (spellAbility == null) {
            StringBuilder msg = new StringBuilder();
            msg.append("AbilityFactory : SpellAbility was not created for ");
            msg.append(hostCard.getName());
            msg.append(". Looking for API: ").append(api);
            throw new RuntimeException(msg.toString());
        }

        // *********************************************
        // set universal properties of the SpellAbility

        spellAbility.setAbilityFactory(this);

        if (hasSubAbility()) {
            spellAbility.setSubAbility(getSubAbility());
        }

        if (spellAbility instanceof Spell_Permanent) {
            spellAbility.setDescription(spellAbility.getSourceCard().getName());
        } else if (hasSpDesc) {
            StringBuilder sb = new StringBuilder();

            if (!isDb) { // SubAbilities don't have Costs or Cost descriptors
                if (mapParams.containsKey("PrecostDesc")) {
                    sb.append(mapParams.get("PrecostDesc")).append(" ");
                }
                if (mapParams.containsKey("CostDesc")) {
                    sb.append(mapParams.get("CostDesc")).append(" ");
                } else {
                    sb.append(abCost.toString());
                }
            }

            sb.append(mapParams.get("SpellDescription"));

            spellAbility.setDescription(sb.toString());
        } else {
            spellAbility.setDescription("");
        }

        // StackDescriptions are overwritten by the AF type instead of through
        // this
        // if (!isTargeted)
        // SA.setStackDescription(hostCard.getName());

        makeRestrictions(spellAbility);
        makeConditions(spellAbility);

        return spellAbility;
    }

    /**
     * <p>
     * makeRestrictions.
     * </p>
     * 
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     */
    private void makeRestrictions(final SpellAbility sa) {
        // SpellAbility_Restrictions should be added in here
        SpellAbility_Restriction restrict = sa.getRestrictions();
        if (mapParams.containsKey("Flashback")) {
            sa.setFlashBackAbility(true);
        }
        restrict.setRestrictions(mapParams);
    }

    /**
     * <p>
     * makeConditions.
     * </p>
     * 
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     */
    private void makeConditions(final SpellAbility sa) {
        // SpellAbility_Restrictions should be added in here
        SpellAbility_Condition condition = sa.getConditions();
        if (mapParams.containsKey("Flashback")) {
            sa.setFlashBackAbility(true);
        }
        condition.setConditions(mapParams);
    }

    /**
     * <p>
     * checkConditional.
     * </p>
     * 
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a boolean.
     */
    public static boolean checkConditional(final SpellAbility sa) {
        return sa.getConditions().checkConditions(sa);
    }

    // Easy creation of SubAbilities
    /**
     * <p>
     * getSubAbility.
     * </p>
     * 
     * @return a {@link forge.card.spellability.Ability_Sub} object.
     */
    public final Ability_Sub getSubAbility() {
        Ability_Sub abSub = null;

        String sSub = getMapParams().get("SubAbility");

        if (sSub.startsWith("SVar=")) {
            sSub = sSub.replace("SVar=", "");
        }

        sSub = getHostCard().getSVar(sSub);

        if (!sSub.equals("")) {
            // Older style Drawback no longer supported
            AbilityFactory afDB = new AbilityFactory();
            abSub = (Ability_Sub) afDB.getAbility(sSub, getHostCard());
        } else {
            System.out.println("SubAbility not found for: " + getHostCard());
        }

        return abSub;
    }

    /**
     * <p>
     * playReusable.
     * </p>
     * 
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a boolean.
     */
    public static boolean playReusable(final SpellAbility sa) {
        // TODO probably also consider if winter orb or similar are out

        if (sa.getPayCosts() == null) {
            return true; // This is only true for Drawbacks and triggers
        }

        if (!sa.getPayCosts().isReusuableResource()) {
            return false;
        }

        if (sa.getRestrictions().getPlaneswalker() && AllZone.getPhase().is(Constant.Phase.Main2)) {
            return true;
        }

        return (AllZone.getPhase().is(Constant.Phase.End_Of_Turn) && AllZone.getPhase().isNextTurn(
                AllZone.getComputerPlayer()));
    }

    // returns true if it's better to wait until blockers are declared
    /**
     * <p>
     * waitForBlocking.
     * </p>
     * 
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a boolean.
     */
    public static boolean waitForBlocking(final SpellAbility sa) {

        return (sa.getSourceCard().isCreature() && sa.getPayCosts().getTap() && (AllZone.getPhase().isBefore(
                Constant.Phase.Combat_Declare_Blockers_InstantAbility) || AllZone.getPhase().isNextTurn(
                AllZone.getHumanPlayer())));
    }

    /**
     * <p>
     * isSorcerySpeed.
     * </p>
     * 
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a boolean.
     */
    public static boolean isSorcerySpeed(final SpellAbility sa) {
        if (sa.isSpell()) {
            return sa.getSourceCard().isSorcery();
        } else if (sa.isAbility()) {
            return sa.getRestrictions().isSorcerySpeed();
        }

        return false;
    }

    // Utility functions used by the AFs
    /**
     * <p>
     * calculateAmount.
     * </p>
     * 
     * @param card
     *            a {@link forge.Card} object.
     * @param amount
     *            a {@link java.lang.String} object.
     * @param ability
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a int.
     */
    public static int calculateAmount(final Card card, String amount, final SpellAbility ability) {
        // amount can be anything, not just 'X' as long as sVar exists

        if (amount == null) {
            return 0;
        }

        // If Amount is -X, strip the minus sign before looking for an SVar of
        // that kind
        int multiplier = 1;
        if (amount.startsWith("-")) {
            multiplier = -1;
            amount = amount.substring(1);
        }

        if (!card.getSVar(amount).equals("")) {
            String[] calcX = card.getSVar(amount).split("\\$");
            if (calcX.length == 1 || calcX[1].equals("none")) {
                return 0;
            }

            if (calcX[0].startsWith("Count")) {
                return CardFactoryUtil.xCount(card, calcX[1]) * multiplier;
            }

            if (calcX[0].startsWith("Number")) {
                return CardFactoryUtil.xCount(card, card.getSVar(amount)) * multiplier;
            } else if (calcX[0].startsWith("SVar")) {
                String[] l = calcX[1].split("/");
                String[] m = CardFactoryUtil.parseMath(l);
                return CardFactoryUtil.doXMath(calculateAmount(card, l[0], ability), m, card) * multiplier;
            } else if (calcX[0].startsWith("Remembered")) {
                // Add whole Remembered list to handlePaid
                CardList list = new CardList();
                for (Object o : card.getRemembered()) {
                    if (o instanceof Card) {
                        list.add(AllZoneUtil.getCardState((Card) o));
                    }
                }

                return CardFactoryUtil.handlePaid(list, calcX[1], card) * multiplier;
            } else if (calcX[0].startsWith("Imprinted")) {
                // Add whole Imprinted list to handlePaid
                CardList list = new CardList();
                for (Card c : card.getImprinted()) {
                    list.add(AllZoneUtil.getCardState(c));
                }

                return CardFactoryUtil.handlePaid(list, calcX[1], card) * multiplier;
            } else if (ability != null) {
                // Player attribute counting
                if (calcX[0].startsWith("TargetedPlayer")) {
                    ArrayList<Player> players = new ArrayList<Player>();
                    SpellAbility saTargeting = (ability.getTarget() == null) ? findParentsTargetedPlayer(ability)
                            : ability;
                    if (saTargeting.getTarget() != null) {
                        players.addAll(saTargeting.getTarget().getTargetPlayers());
                    } else {
                        players.addAll(getDefinedPlayers(card,
                                saTargeting.getAbilityFactory().getMapParams().get("Defined"), saTargeting));
                    }
                    return CardFactoryUtil.playerXCount(players, calcX[1], card) * multiplier;
                }
                if (calcX[0].startsWith("TargetedController")) {
                    ArrayList<Player> players = new ArrayList<Player>();
                    ArrayList<Card> list = getDefinedCards(card, "Targeted", ability);
                    ArrayList<SpellAbility> sas = getDefinedSpellAbilities(card, "Targeted", ability);

                    for (Card c : list) {
                        Player p = c.getController();
                        if (!players.contains(p)) {
                            players.add(p);
                        }
                    }
                    for (SpellAbility s : sas) {
                        Player p = s.getSourceCard().getController();
                        if (!players.contains(p)) {
                            players.add(p);
                        }
                    }
                    return CardFactoryUtil.playerXCount(players, calcX[1], card) * multiplier;
                }

                CardList list = new CardList();
                if (calcX[0].startsWith("Sacrificed")) {
                    list = findRootAbility(ability).getPaidList("Sacrificed");
                } else if (calcX[0].startsWith("Discarded")) {
                    list = findRootAbility(ability).getPaidList("Discarded");
                } else if (calcX[0].startsWith("Exiled")) {
                    list = findRootAbility(ability).getPaidList("Exiled");
                } else if (calcX[0].startsWith("Tapped")) {
                    list = findRootAbility(ability).getPaidList("Tapped");
                } else if (calcX[0].startsWith("Revealed")) {
                    list = findRootAbility(ability).getPaidList("Revealed");
                } else if (calcX[0].startsWith("Targeted")) {
                    Target t = ability.getTarget();
                    if (null != t) {
                        ArrayList<Object> all = t.getTargets();
                        if (!all.isEmpty() && all.get(0) instanceof SpellAbility) {
                            SpellAbility saTargeting = findParentsTargetedSpellAbility(ability);
                            list = new CardList();
                            ArrayList<SpellAbility> sas = saTargeting.getTarget().getTargetSAs();
                            for (SpellAbility sa : sas) {
                                list.add(sa.getSourceCard());
                            }
                        } else {
                            SpellAbility saTargeting = findParentsTargetedCard(ability);
                            list = new CardList(saTargeting.getTarget().getTargetCards().toArray());
                        }
                    } else {
                        SpellAbility parent = findParentsTargetedCard(ability);

                        ArrayList<Object> all = parent.getTarget().getTargets();
                        if (!all.isEmpty() && all.get(0) instanceof SpellAbility) {
                            list = new CardList();
                            ArrayList<SpellAbility> sas = parent.getTarget().getTargetSAs();
                            for (SpellAbility sa : sas) {
                                list.add(sa.getSourceCard());
                            }
                        } else {
                            SpellAbility saTargeting = findParentsTargetedCard(ability);
                            list = new CardList(saTargeting.getTarget().getTargetCards().toArray());
                        }
                    }
                } else if (calcX[0].startsWith("Triggered")) {
                    SpellAbility root = ability.getRootSpellAbility();
                    list = new CardList();
                    list.add((Card) root.getTriggeringObject(calcX[0].substring(9)));
                } else if (calcX[0].startsWith("TriggerCount")) {
                    // TriggerCount is similar to a regular Count, but just
                    // pulls Integer Values from Trigger objects
                    SpellAbility root = ability.getRootSpellAbility();
                    String[] l = calcX[1].split("/");
                    String[] m = CardFactoryUtil.parseMath(l);
                    int count = (Integer) root.getTriggeringObject(l[0]);

                    return CardFactoryUtil.doXMath(count, m, card) * multiplier;
                } else {

                    return 0;
                }

                return CardFactoryUtil.handlePaid(list, calcX[1], card) * multiplier;

            } else {
                return 0;
            }
        }
        if (amount.equals("ChosenX")) {
            // isn't made yet
            return 0;
        }

        return Integer.parseInt(amount) * multiplier;
    }

    // should the three getDefined functions be merged into one? Or better to
    // have separate?
    // If we only have one, each function needs to Cast the Object to the
    // appropriate type when using
    // But then we only need update one function at a time once the casting is
    // everywhere.
    // Probably will move to One function solution sometime in the future
    /**
     * <p>
     * getDefinedCards.
     * </p>
     * 
     * @param hostCard
     *            a {@link forge.Card} object.
     * @param def
     *            a {@link java.lang.String} object.
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a {@link java.util.ArrayList} object.
     */
    public static ArrayList<Card> getDefinedCards(final Card hostCard, final String def, final SpellAbility sa) {
        ArrayList<Card> cards = new ArrayList<Card>();
        String defined = (def == null) ? "Self" : def; // default to Self

        Card c = null;

        if (defined.equals("Self")) {
            c = hostCard;
        }

        else if (defined.equals("Equipped")) {
            c = hostCard.getEquippingCard();
        }

        else if (defined.equals("Enchanted")) {
            c = hostCard.getEnchantingCard();
        } else if (defined.equals("TopOfLibrary")) {
            CardList lib = hostCard.getController().getCardsIn(Constant.Zone.Library);
            if (lib.size() > 0) {
                c = lib.get(0);
            } else {
                // we don't want this to fall through and return the "Self"
                return new ArrayList<Card>();
            }
        }

        else if (defined.equals("Targeted")) {
            SpellAbility parent = findParentsTargetedCard(sa);
            cards.addAll(parent.getTarget().getTargetCards());
        } else if (defined.startsWith("Triggered") && sa != null) {
            SpellAbility root = sa.getRootSpellAbility();
            Object crd = root.getTriggeringObject(defined.substring(9));
            if (crd instanceof Card) {
                c = AllZoneUtil.getCardState((Card) crd);
            } else if (crd instanceof CardList) {
                for (Card cardItem : (CardList) crd) {
                    cards.add(cardItem);
                }
            }
        } else if (defined.equals("Remembered")) {
            for (Object o : hostCard.getRemembered()) {
                if (o instanceof Card) {
                    cards.add(AllZoneUtil.getCardState((Card) o));
                }
            }
        } else if (defined.equals("Clones")) {
            for (Card clone : hostCard.getClones()) {
                cards.add(AllZoneUtil.getCardState(clone));
            }
        } else if (defined.equals("Imprinted")) {
            for (Card imprint : hostCard.getImprinted()) {
                cards.add(AllZoneUtil.getCardState(imprint));
            }
        } else if (defined.startsWith("ThisTurnEntered")) {
            String[] workingCopy = defined.split(" ");
            Zone destination, origin;
            String validFilter;

            destination = Zone.smartValueOf(workingCopy[1]);
            if (workingCopy[2].equals("from")) {
                origin = Zone.smartValueOf(workingCopy[3]);
                validFilter = workingCopy[4];
            } else {
                origin = null;
                validFilter = workingCopy[2];
            }
            for (Card cl : CardUtil.getThisTurnEntered(destination, origin, validFilter, hostCard)) {
                cards.add(cl);
            }
        } else {
            CardList list = null;
            if (defined.startsWith("Sacrificed")) {
                list = findRootAbility(sa).getPaidList("Sacrificed");
            }

            else if (defined.startsWith("Discarded")) {
                list = findRootAbility(sa).getPaidList("Discarded");
            }

            else if (defined.startsWith("Exiled")) {
                list = findRootAbility(sa).getPaidList("Exiled");
            }

            else if (defined.startsWith("Tapped")) {
                list = findRootAbility(sa).getPaidList("Tapped");
            }

            else {
                return cards;
            }

            for (Card cl : list) {
                cards.add(cl);
            }
        }

        if (c != null) {
            cards.add(c);
        }

        return cards;
    }

    /**
     * <p>
     * getDefinedPlayers.
     * </p>
     * 
     * @param card
     *            a {@link forge.Card} object.
     * @param def
     *            a {@link java.lang.String} object.
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a {@link java.util.ArrayList} object.
     */
    public static ArrayList<Player> getDefinedPlayers(final Card card, final String def, final SpellAbility sa) {
        ArrayList<Player> players = new ArrayList<Player>();
        String defined = (def == null) ? "You" : def;

        if (defined.equals("Targeted")) {
            Target tgt = sa.getTarget();
            SpellAbility parent = sa;

            do {

                // did not find any targets
                if (!(parent instanceof Ability_Sub)) {
                    return players;
                }
                parent = ((Ability_Sub) parent).getParent();
                tgt = parent.getTarget();
            } while (tgt == null || tgt.getTargetPlayers().size() == 0);

            players.addAll(tgt.getTargetPlayers());
        } else if (defined.equals("TargetedController")) {
            ArrayList<Card> list = getDefinedCards(card, "Targeted", sa);
            ArrayList<SpellAbility> sas = getDefinedSpellAbilities(card, "Targeted", sa);

            for (Card c : list) {
                Player p = c.getController();
                if (!players.contains(p)) {
                    players.add(p);
                }
            }
            for (SpellAbility s : sas) {
                Player p = s.getSourceCard().getController();
                if (!players.contains(p)) {
                    players.add(p);
                }
            }
        } else if (defined.equals("TargetedOwner")) {
            ArrayList<Card> list = getDefinedCards(card, "Targeted", sa);

            for (Card c : list) {
                Player p = c.getOwner();
                if (!players.contains(p)) {
                    players.add(p);
                }
            }
        } else if (defined.equals("Remembered")) {
            for (Object rem : card.getRemembered()) {
                if (rem instanceof Player) {
                    players.add((Player) rem);
                }
            }
        } else if (defined.startsWith("Triggered")) {
            SpellAbility root = sa.getRootSpellAbility();
            Object o = null;
            if (defined.endsWith("Controller")) {
                String triggeringType = defined.substring(9);
                triggeringType = triggeringType.substring(0, triggeringType.length() - 10);
                Object c = root.getTriggeringObject(triggeringType);
                if (c instanceof Card) {
                    o = ((Card) c).getController();
                }
                if (c instanceof SpellAbility) {
                    o = ((SpellAbility) c).getSourceCard().getController();
                }
            } else if (defined.endsWith("Owner")) {
                String triggeringType = defined.substring(9);
                triggeringType = triggeringType.substring(0, triggeringType.length() - 5);
                Object c = root.getTriggeringObject(triggeringType);
                if (c instanceof Card) {
                    o = ((Card) c).getOwner();
                }
            } else {
                String triggeringType = defined.substring(9);
                o = root.getTriggeringObject(triggeringType);
            }
            if (o != null) {
                if (o instanceof Player) {
                    Player p = (Player) o;
                    if (!players.contains(p)) {
                        players.add(p);
                    }
                }
            }
        } else if (defined.equals("EnchantedController")) {
            Player p = card.getEnchantingCard().getController();
            if (!players.contains(p)) {
                players.add(p);
            }
        } else if (defined.equals("EnchantedOwner")) {
            Player p = card.getEnchantingCard().getOwner();
            if (!players.contains(p)) {
                players.add(p);
            }
        } else if (defined.equals("EnchantedPlayer")) {
            Object o = sa.getSourceCard().getEnchanting();
            if (o instanceof Player) {
                if (!players.contains((Player) o)) {
                    players.add((Player) o);
                }
            }
        } else if (defined.equals("AttackingPlayer")) {
            Player p = AllZone.getCombat().getAttackingPlayer();
            if (!players.contains(p)) {
                players.add(p);
            }
        } else if (defined.equals("DefendingPlayer")) {
            Player p = AllZone.getCombat().getDefendingPlayer();
            if (!players.contains(p)) {
                players.add(p);
            }
        } else if (defined.equals("ChosenPlayer")) {
            Player p = card.getChosenPlayer();
            if (!players.contains(p)) {
                players.add(p);
            }
        } else {
            if (defined.equals("You") || defined.equals("Each")) {
                players.add(sa.getActivatingPlayer());
            }

            if (defined.equals("Opponent") || defined.equals("Each")) {
                players.add(sa.getActivatingPlayer().getOpponent());
            }
        }
        return players;
    }

    /**
     * <p>
     * getDefinedSpellAbilities.
     * </p>
     * 
     * @param card
     *            a {@link forge.Card} object.
     * @param def
     *            a {@link java.lang.String} object.
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a {@link java.util.ArrayList} object.
     */
    public static ArrayList<SpellAbility> getDefinedSpellAbilities(final Card card, final String def,
            final SpellAbility sa) {
        ArrayList<SpellAbility> sas = new ArrayList<SpellAbility>();
        String defined = (def == null) ? "Self" : def; // default to Self

        SpellAbility s = null;

        // TODO - this probably needs to be fleshed out a bit, but the basics
        // work
        if (defined.equals("Self")) {
            s = sa;
        } else if (defined.equals("Targeted")) {
            SpellAbility parent = findParentsTargetedSpellAbility(sa);
            sas.addAll(parent.getTarget().getTargetSAs());
        } else if (defined.startsWith("Triggered")) {
            SpellAbility root = sa.getRootSpellAbility();

            String triggeringType = defined.substring(9);
            Object o = root.getTriggeringObject(triggeringType);
            if (o instanceof SpellAbility) {
                s = (SpellAbility) o;
            }
        } else if (defined.startsWith("Imprinted")) {
            for (Card imp : card.getImprinted()) {
                sas.addAll(imp.getSpellAbilities());
            }
        }

        if (s != null) {
            sas.add(s);
        }

        return sas;
    }

    /**
     * <p>
     * getDefinedObjects.
     * </p>
     * 
     * @param card
     *            a {@link forge.Card} object.
     * @param def
     *            a {@link java.lang.String} object.
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a {@link java.util.ArrayList} object.
     */
    public static ArrayList<Object> getDefinedObjects(final Card card, final String def, final SpellAbility sa) {
        ArrayList<Object> objects = new ArrayList<Object>();
        String defined = (def == null) ? "Self" : def;

        objects.addAll(getDefinedPlayers(card, defined, sa));
        objects.addAll(getDefinedCards(card, defined, sa));
        objects.addAll(getDefinedSpellAbilities(card, defined, sa));
        return objects;
    }

    /**
     * <p>
     * findRootAbility.
     * </p>
     * 
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility findRootAbility(final SpellAbility sa) {
        SpellAbility parent = sa;
        while (parent instanceof Ability_Sub) {
            parent = ((Ability_Sub) parent).getParent();
        }

        return parent;
    }

    /**
     * <p>
     * findParentsTargetedCard.
     * </p>
     * 
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility findParentsTargetedCard(final SpellAbility sa) {
        SpellAbility parent = sa;

        do {
            if (!(parent instanceof Ability_Sub)) {
                return parent;
            }
            parent = ((Ability_Sub) parent).getParent();
        } while (parent.getTarget() == null || parent.getTarget().getTargetCards().size() == 0);

        return parent;
    }

    /**
     * <p>
     * findParentsTargetedSpellAbility.
     * </p>
     * 
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    private static SpellAbility findParentsTargetedSpellAbility(final SpellAbility sa) {
        SpellAbility parent = sa;

        do {
            if (!(parent instanceof Ability_Sub)) {
                return parent;
            }
            parent = ((Ability_Sub) parent).getParent();
        } while (parent.getTarget() == null || parent.getTarget().getTargetSAs().size() == 0);

        return parent;
    }

    /**
     * <p>
     * findParentsTargetedPlayer.
     * </p>
     * 
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a {@link forge.card.spellability.SpellAbility} object.
     */
    public static SpellAbility findParentsTargetedPlayer(final SpellAbility sa) {
        SpellAbility parent = sa;

        do {
            if (!(parent instanceof Ability_Sub)) {
                return parent;
            }
            parent = ((Ability_Sub) parent).getParent();
        } while (parent.getTarget() == null || parent.getTarget().getTargetPlayers().size() == 0);

        return parent;
    }

    /**
     * <p>
     * predictThreatenedObjects.
     * </p>
     * 
     * @param saviourAf
     *            a AbilityFactory object
     * @return a {@link java.util.ArrayList} object.
     * @since 1.0.15
     */
    public static ArrayList<Object> predictThreatenedObjects(final AbilityFactory saviourAf) {
        ArrayList<Object> objects = new ArrayList<Object>();
        if (AllZone.getStack().size() == 0) {
            return objects;
        }

        // check stack for something that will kill this
        SpellAbility topStack = AllZone.getStack().peekAbility();
        objects.addAll(predictThreatenedObjects(saviourAf, topStack));

        return objects;
    }

    /**
     * <p>
     * predictThreatenedObjects.
     * </p>
     * 
     * @param saviourAf
     *            a AbilityFactory object
     * @param topStack
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @return a {@link java.util.ArrayList} object.
     * @since 1.0.15
     */
    public static ArrayList<Object> predictThreatenedObjects(final AbilityFactory saviourAf, final SpellAbility topStack) {
        ArrayList<Object> objects = new ArrayList<Object>();
        ArrayList<Object> threatened = new ArrayList<Object>();
        String saviourApi = "";
        if (saviourAf != null) {
            saviourApi = saviourAf.getAPI();
        }

        if (topStack == null) {
            return objects;
        }

        Card source = topStack.getSourceCard();
        AbilityFactory topAf = topStack.getAbilityFactory();

        // Can only Predict things from AFs
        if (topAf != null) {
            Target tgt = topStack.getTarget();

            if (tgt == null) {
                objects = getDefinedObjects(source, topAf.getMapParams().get("Defined"), topStack);
            } else {
                objects = tgt.getTargets();
            }

            // Determine if Defined Objects are "threatened" will be destroyed
            // due to this SA

            String threatApi = topAf.getAPI();
            HashMap<String, String> threatParams = topAf.getMapParams();

            // Lethal Damage => prevent damage/regeneration/bounce
            if (threatApi.equals("DealDamage") || threatApi.equals("DamageAll")) {
                // If PredictDamage is >= Lethal Damage
                int dmg = AbilityFactory.calculateAmount(topStack.getSourceCard(), topAf.getMapParams().get("NumDmg"),
                        topStack);
                for (Object o : objects) {
                    if (o instanceof Card) {
                        Card c = (Card) o;

                        // indestructible
                        if (c.hasKeyword("Indestructible")) {
                            continue;
                        }

                        // already regenerated
                        if (c.getShield() > 0) {
                            continue;
                        }

                        // don't use it on creatures that can't be regenerated
                        if (saviourApi.equals("Regenerate") && !c.canBeShielded()) {
                            continue;
                        }

                        // don't bounce or blink a permanent that the human
                        // player owns or is a token
                        if (saviourApi.equals("ChangeZone") && (c.getOwner().isHuman() || c.isToken())) {
                            continue;
                        }

                        if (c.predictDamage(dmg, source, false) >= c.getKillDamage()) {
                            threatened.add(c);
                        }
                    } else if (o instanceof Player) {
                        Player p = (Player) o;

                        if (source.hasKeyword("Infect")) {
                            if (p.predictDamage(dmg, source, false) >= p.getPoisonCounters()) {
                                threatened.add(p);
                            }
                        } else if (p.predictDamage(dmg, source, false) >= p.getLife()) {
                            threatened.add(p);
                        }
                    }
                }
            }
            // Destroy => regeneration/bounce
            else if ((threatApi.equals("Destroy") || threatApi.equals("DestroyAll"))
                    && ((saviourApi.equals("Regenerate") && !threatParams.containsKey("NoRegen")) || saviourApi
                            .equals("ChangeZone")))
            {
                for (Object o : objects) {
                    if (o instanceof Card) {
                        Card c = (Card) o;
                        // indestructible
                        if (c.hasKeyword("Indestructible")) {
                            continue;
                        }

                        // already regenerated
                        if (c.getShield() > 0) {
                            continue;
                        }

                        // don't bounce or blink a permanent that the human
                        // player owns or is a token
                        if (saviourApi.equals("ChangeZone") && (c.getOwner().isHuman() || c.isToken())) {
                            continue;
                        }

                        // don't use it on creatures that can't be regenerated
                        if (saviourApi.equals("Regenerate") && !c.canBeShielded()) {
                            continue;
                        }
                        threatened.add(c);
                    }
                }
            }
            // Exiling => bounce
            else if ((threatApi.equals("ChangeZone") || threatApi.equals("ChangeZoneAll"))
                    && saviourApi.equals("ChangeZone") && threatParams.containsKey("Destination")
                    && threatParams.get("Destination").equals("Exile"))
            {
                for (Object o : objects) {
                    if (o instanceof Card) {
                        Card c = (Card) o;
                        // don't bounce or blink a permanent that the human
                        // player owns or is a token
                        if (saviourApi.equals("ChangeZone") && (c.getOwner().isHuman() || c.isToken())) {
                            continue;
                        }

                        threatened.add(c);
                    }
                }
            }
        }

        threatened.addAll(predictThreatenedObjects(saviourAf, topStack.getSubAbility()));
        return threatened;
    }

    /**
     * <p>
     * handleRemembering.
     * </p>
     * 
     * @param af
     *            a {@link forge.card.abilityFactory.AbilityFactory} object.
     */
    public static void handleRemembering(final AbilityFactory af) {
        HashMap<String, String> params = af.getMapParams();
        Card host;

        if (!params.containsKey("RememberTargets")) {
            return;
        }

        host = af.getHostCard();

        if (params.containsKey("ForgetOtherTargets")) {
            host.clearRemembered();
        }

        Target tgt = af.getAbTgt();

        if (params.containsKey("RememberTargets")) {
            ArrayList<Object> tgts = (tgt == null) ? new ArrayList<Object>() : tgt.getTargets();
            for (Object o : tgts) {
                host.addRemembered(o);
            }
        }
    }

    /**
     * Filter list by type.
     * 
     * @param list
     *            a CardList
     * @param type
     *            a card type
     * @param sa
     *            a SpellAbility
     * @return a {@link forge.CardList} object.
     */
    public static CardList filterListByType(final CardList list, String type, final SpellAbility sa) {
        if (type == null) {
            return list;
        }

        // Filter List Can send a different Source card in for things like
        // Mishra and Lobotomy

        Card source = sa.getSourceCard();
        if (type.contains("Triggered")) {
            Object o = sa.getTriggeringObject("Card");

            // I won't the card attached to the Triggering object
            if (!(o instanceof Card)) {
                return new CardList();
            }

            source = (Card) (o);
            type = type.replace("Triggered", "Card");
        } else if (type.startsWith("Remembered")) {
            boolean hasRememberedCard = false;
            for (Object o : source.getRemembered()) {
                if (o instanceof Card) {
                    hasRememberedCard = true;
                    source = (Card) o;
                    type = type.replace("Remembered", "Card");
                    break;
                }
            }

            if (!hasRememberedCard) {
                return new CardList();
            }
        }

        return list.getValidCards(type.split(","), sa.getActivatingPlayer(), source);
    }

    /**
     * <p>
     * passUnlessCost.
     * </p>
     * 
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @param usedStack
     *            a boolean.
     */
    public static void passUnlessCost(final SpellAbility sa, final boolean usedStack) {
        Card source = sa.getSourceCard();
        AbilityFactory af = sa.getAbilityFactory();
        final HashMap<String, String> params = af.getMapParams();

        // Nothing to do
        if (params.get("UnlessCost") == null) {
            sa.resolve();
            return;
        }

        // The player who has the chance to cancel the ability
        String pays = params.containsKey("UnlessPayer") ? params.get("UnlessPayer") : "TargetedController";
        Player payer = getDefinedPlayers(sa.getSourceCard(), pays, sa).get(0);

        // The cost
        String unlessCost = params.get("UnlessCost").trim();
        if (unlessCost.equals("X")) {
            unlessCost = Integer.toString(AbilityFactory.calculateAmount(source, params.get("UnlessCost"), sa));
        }

        Ability ability = new Ability(source, unlessCost) {
            @Override
            public void resolve() {
                // nothing to do
            }
        };

        final Command paidCommand = new Command() {
            private static final long serialVersionUID = 8094833091127334678L;

            public void execute() {
                resolveSubAbilities(sa);
                if (usedStack) {
                    AllZone.getStack().finishResolving(sa, false);
                }
            }
        };

        final Command unpaidCommand = new Command() {
            private static final long serialVersionUID = 8094833091127334678L;

            public void execute() {
                sa.resolve();
                if (params.containsKey("PowerSink")) {
                    GameActionUtil.doPowerSink(AllZone.getHumanPlayer());
                }
                resolveSubAbilities(sa);
                if (usedStack) {
                    AllZone.getStack().finishResolving(sa, false);
                }
            }
        };

        if (payer.isHuman()) {
            GameActionUtil.payManaDuringAbilityResolve(source + "\r\n", ability.getManaCost(), paidCommand,
                    unpaidCommand);
        } else {
            if (ComputerUtil.canPayCost(ability)) {
                ComputerUtil.playNoStack(ability); // Unless cost was payed - no
                                                   // resolve
                resolveSubAbilities(sa);
                if (usedStack) {
                    AllZone.getStack().finishResolving(sa, false);
                }
            } else {
                sa.resolve();
                if (params.containsKey("PowerSink")) {
                    GameActionUtil.doPowerSink(AllZone.getComputerPlayer());
                }
                resolveSubAbilities(sa);
                if (usedStack) {
                    AllZone.getStack().finishResolving(sa, false);
                }
            }
        }
    }

    /**
     * <p>
     * resolve.
     * </p>
     * 
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @param usedStack
     *            a boolean.
     */
    public static void resolve(final SpellAbility sa, final boolean usedStack) {
        if (sa == null) {
            return;
        }
        AbilityFactory af = sa.getAbilityFactory();
        if (af == null) {
            sa.resolve();
            return;
        }
        HashMap<String, String> params = af.getMapParams();

        // check conditions
        if (AbilityFactory.checkConditional(sa)) {
            if (params.get("UnlessCost") == null || sa.isWrapper()) {
                sa.resolve();

                // try to resolve subabilities (see null check above)
                resolveSubAbilities(sa);
                if (usedStack) {
                    AllZone.getStack().finishResolving(sa, false);
                }
            } else {
                passUnlessCost(sa, usedStack);
            }
        } else {
            resolveSubAbilities(sa);
            if (usedStack) {
                AllZone.getStack().finishResolving(sa, false);
            }
        }
    }

    /**
     * <p>
     * resolveSubAbilities.
     * </p>
     * 
     * @param sa
     *            a {@link forge.card.spellability.SpellAbility} object.
     * @since 1.0.15
     */
    public static void resolveSubAbilities(final SpellAbility sa) {
        Ability_Sub abSub = sa.getSubAbility();
        if (abSub == null || sa.isWrapper()) {
            return;
        }
        // check conditions
        if (AbilityFactory.checkConditional(abSub)) {
            abSub.resolve();
        }
        resolveSubAbilities(abSub);
    }

} // end class AbilityFactory
