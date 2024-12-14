package co.dotarch.x;

import co.dotarch.x.events.DotSignalEvent;
import co.dotarch.x.plugin.DotX;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public abstract class FeaturePack extends DotPlugin
{
    /**
     * Gets the key associated - Return whatever identifer your pack uses in dotSignals
     * @return Your pack's key
     */
    public abstract String getKey();

    @Override
    public void onEnable()
    {
        super.onEnable();
        DotX.api().registerFeaturePack(this);
    }

    /**
     * Use a feature.
     * @param feature Key of the feature within this featurepack.
     * @return DotSignalEvent with the state of the feature call, including results and cancellation.
     */
    public abstract void useFeatures(String feature, DotSignalEvent event);

    @EventHandler
    public void onDotSignal(DotSignalEvent event)
    {
        var potentialFeature = event.getSymbol("feature");
        if(potentialFeature instanceof String)
        {
            useFeatures((String) potentialFeature, event);
        }
    }

}
