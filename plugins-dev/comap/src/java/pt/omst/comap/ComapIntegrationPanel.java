package pt.omst.comap;

import java.util.Vector;

import pt.lsts.imc.GeoFeature;
import pt.lsts.imc.MapPoint;
import pt.lsts.imc.SurveyTask;
import pt.lsts.imc.SynchAdmin;
import pt.lsts.imc.TaskAdim;
import pt.lsts.imc.WorldModel;
import pt.lsts.imc.SynchAdmin.OP;
import pt.lsts.imc.def.SensorType;
import pt.lsts.neptus.comm.manager.imc.ImcMsgManager;
import pt.lsts.neptus.console.ConsoleLayout;
import pt.lsts.neptus.console.ConsolePanel;
import pt.lsts.neptus.plugins.PluginDescription;
import pt.lsts.neptus.types.coord.LocationType;
import pt.lsts.neptus.types.map.AbstractElement;
import pt.lsts.neptus.types.map.PathElement;

/**
 * @author zp
 *
 */
@PluginDescription(name = "Comap Integration")
public class ComapIntegrationPanel extends ConsolePanel {

    private WorldModel model = null;

    public ComapIntegrationPanel(ConsoleLayout console) {
        super(console);        
    }

    synchronized WorldModel getModel() {
        if (model == null) {
            int feature_id = 1;
            model = new WorldModel();
            Vector<GeoFeature> features = new Vector<>();

            for (PathElement el : getConsole().getMission().generateMapGroup().getAllObjectsOfType(PathElement.class)) {
                System.out.println("processing "+el.getId());
                Vector<LocationType> points = el.getShapePoints();
                System.out.println(points);
                if (points == null || points.size() <3)
                    continue;
                Vector<MapPoint> featurePoints = new Vector<>();
                GeoFeature feature = new GeoFeature();
                feature.setFeatureId(feature_id++);
                for (LocationType loc : points)
                    featurePoints.add(new MapPoint(loc.getLatitudeRads(), loc.getLongitudeRads(), 0));
                feature.setPoints(featurePoints);
                features.add(feature);
            }
            model.setGeoFeatures(features);
        }
        return model;
    }

    static int feature_id = 1;
    @Override
    public void initSubPanel() {        
        addMenuItem("Tools>CoMap>Send SynchAdmin/HOLD", null, a -> {
            SynchAdmin msg = new SynchAdmin();
            msg.setOp(OP.HOLD);
            ImcMsgManager.getManager().sendMessageToSystem(msg, getConsole().getMainSystem());
        });

        addMenuItem("Tools>CoMap>Send SynchAdmin/RESUME", null, a -> {
            SynchAdmin msg = new SynchAdmin();
            msg.setOp(OP.RESUME);
            ImcMsgManager.getManager().sendMessageToSystem(msg, getConsole().getMainSystem());
        });
    
        addMenuItem("Tools>CoMap>Send World Model", null, a -> {
            ImcMsgManager.getManager().sendMessageToSystem(getModel(), getConsole().getMainSystem());
        });

        addMenuItem("Tools>CoMap>Send TaskAdmin Assign", null, a -> {
            TaskAdim msg = new TaskAdim();
            msg.setTid(feature_id);
            msg.setOp(TaskAdim.OP.ASSIGN);
            SurveyTask st = new SurveyTask(feature_id, feature_id, SensorType.SIDESCAN, 750, System.currentTimeMillis() / 1000.0 + 3600.0);
            msg.setArg(st);
            feature_id++;
            ImcMsgManager.getManager().sendMessageToSystem(msg, getConsole().getMainSystem());
        });

        addMenuItem("Tools>CoMap>Send TaskAdmin Unassign", null, a -> {
            feature_id--;
            TaskAdim msg = new TaskAdim();
            msg.setTid(feature_id);
            msg.setOp(TaskAdim.OP.UNASSIGN);
            ImcMsgManager.getManager().sendMessageToSystem(msg, getConsole().getMainSystem());
        });
        
    }

    @Override
    public void cleanSubPanel() {        
        
    }

}