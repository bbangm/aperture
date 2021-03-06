package mchorse.aperture.camera;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.camera.destination.AbstractDestination;
import mchorse.aperture.client.gui.GuiCameraEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Camera control class
 *
 * This class is responsible for controlling the camera profile. The actions
 * are delegated from keyboard handler.
 */
@SideOnly(Side.CLIENT)
public class CameraControl
{
    /**
     * Currently stored camera profiles 
     */
    public List<CameraProfile> profiles = new ArrayList<CameraProfile>();

    /**
     * Currently rendered/editing camera profile
     */
    public CameraProfile currentProfile;

    /**
     * Roll of the camera
     */
    public float roll = 0;

    /**
     * Was player logged in. Used to add a default camera profile in a 
     * new world.
     */
    public boolean logged;

    /**
     * Reset camera profiles 
     */
    public void reset()
    {
        /* Saving dirty camera profiles */
        for (CameraProfile profile : this.profiles)
        {
            if (profile.dirty)
            {
                profile.save();
            }
        }

        this.profiles.clear();
        this.currentProfile = null;
        this.logged = false;
    }

    /**
     * Add a camera profile to the list of loaded camera profiles and also set 
     * it current. 
     */
    public void addProfile(CameraProfile profile)
    {
        this.insertProfile(profile);
        this.currentProfile = profile;

        GuiScreen screen = Minecraft.getMinecraft().currentScreen;

        if (screen instanceof GuiCameraEditor)
        {
            ((GuiCameraEditor) screen).selectProfile(profile);
        }
    }

    /**
     * Remove camera profile 
     */
    public void removeProfile(CameraProfile profile)
    {
        this.profiles.remove(profile);

        if (profile == this.currentProfile)
        {
            this.currentProfile = null;

            GuiScreen screen = Minecraft.getMinecraft().currentScreen;

            if (screen instanceof GuiCameraEditor)
            {
                ((GuiCameraEditor) screen).selectProfile(null);
            }
        }
    }

    /**
     * Insert camera profile (just add it to the list of camera profiles) 
     */
    public void insertProfile(CameraProfile newProfile)
    {
        Iterator<CameraProfile> it = this.profiles.iterator();

        while (it.hasNext())
        {
            CameraProfile profile = it.next();

            if (profile.getDestination().equals(newProfile.getDestination()))
            {
                it.remove();
            }
        }

        this.profiles.add(newProfile);
    }

    /**
     * Is there a camera profile which has same destination 
     */
    public boolean hasSimilar(AbstractDestination destination)
    {
        Iterator<CameraProfile> it = this.profiles.iterator();

        while (it.hasNext())
        {
            CameraProfile profile = it.next();

            if (profile.getDestination().equals(destination))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Get camera profile by given filename 
     */
    public CameraProfile getProfile(AbstractDestination dest)
    {
        for (CameraProfile profile : this.profiles)
        {
            if (profile.getDestination().equals(dest))
            {
                return profile;
            }
        }

        return null;
    }

    /**
     * Add roll (it can be negative too)
     */
    public void setRoll(float value)
    {
        ClientProxy.renderer.roll.reset(value);
        this.roll = value;
    }

    /**
     * Reset roll (set it back to 0)
     */
    public void resetRoll()
    {
        this.setRoll(0.0F);
    }

    /**
     * Set FOV
     */
    public void setFOV(float value)
    {
        ClientProxy.renderer.fov.reset(value);
        Minecraft.getMinecraft().gameSettings.fovSetting = value;
    }

    /**
     * Reset FOV to default value
     */
    public void resetFOV()
    {
        this.setFOV(70.0F);
    }

    /**
     * Set both roll and FOV at the same time
     */
    public void setRollAndFOV(float roll, float fov)
    {
        this.setRoll(roll);
        this.setFOV(fov);
    }
}