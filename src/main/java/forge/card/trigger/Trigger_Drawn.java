package forge.card.trigger;

import java.util.HashMap;

import forge.Card;
import forge.card.spellability.SpellAbility;

/**
 * <p>
 * Trigger_Drawn class.
 * </p>
 * 
 * @author Forge
 * @version $Id$
 */
public class Trigger_Drawn extends Trigger {

    /**
     * <p>
     * Constructor for Trigger_Drawn.
     * </p>
     * 
     * @param params
     *            a {@link java.util.HashMap} object.
     * @param host
     *            a {@link forge.Card} object.
     * @param intrinsic
     *            the intrinsic
     */
    public Trigger_Drawn(final HashMap<String, String> params, final Card host, final boolean intrinsic) {
        super(params, host, intrinsic);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean performTest(final java.util.Map<String, Object> runParams2) {
        final Card draw = ((Card) runParams2.get("Card"));

        if (this.getMapParams().containsKey("ValidCard")) {
            if (!draw.isValid(this.getMapParams().get("ValidCard").split(","), this.getHostCard().getController(),
                    this.getHostCard())) {
                return false;
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public final Trigger getCopy() {
        final Trigger copy = new Trigger_Drawn(this.getMapParams(), this.getHostCard(), this.isIntrinsic());
        if (this.getOverridingAbility() != null) {
            copy.setOverridingAbility(this.getOverridingAbility());
        }
        copy.setName(this.getName());
        copy.setID(this.getId());

        return copy;
    }

    /** {@inheritDoc} */
    @Override
    public final void setTriggeringObjects(final SpellAbility sa) {
        sa.setTriggeringObject("Card", this.getRunParams().get("Card"));
    }
}
