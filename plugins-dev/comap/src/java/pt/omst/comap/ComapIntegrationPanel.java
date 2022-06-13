package pt.omst.comap;

import java.util.Optional;
import java.util.Vector;

import pt.lsts.imc.GeoFeature;
import pt.lsts.imc.MapPoint;
import pt.lsts.imc.MoveTask;
import pt.lsts.imc.SurveyTask;
import pt.lsts.imc.SynchAdmin;
import pt.lsts.imc.SynchAdmin.OP;
import pt.lsts.imc.TaskAdim;
import pt.lsts.imc.TaskAdminArgs;
import pt.lsts.imc.WorldModel;
import pt.lsts.imc.def.SensorType;
import pt.lsts.neptus.NeptusLog;
import pt.lsts.neptus.comm.manager.imc.ImcMsgManager;
import pt.lsts.neptus.console.ConsoleLayout;
import pt.lsts.neptus.console.ConsolePanel;
import pt.lsts.neptus.plugins.PluginDescription;
import pt.lsts.neptus.types.coord.LocationType;
import pt.lsts.neptus.types.map.MarkElement;
import pt.lsts.neptus.types.map.PathElement;

/**
 * @author zp
 *
 */
@PluginDescription(name = "Comap Integration")
public class ComapIntegrationPanel extends ConsolePanel {

    private WorldModel model = null;
    int feature_id = 1;

    public ComapIntegrationPanel(ConsoleLayout console) {
        super(console);
    }

    private Optional<TaskAdminArgs> getTask(int id) {
        if (model == null)
            return Optional.empty();
        for (GeoFeature f : model.getGeoFeatures()) {
            if (f.getFeatureId() == id) {
                if (f.getPoints().size() > 1) {
                    return Optional.of(new SurveyTask(id, id, SensorType.SIDESCAN, 750,
                            System.currentTimeMillis() / 1000.0 + 80000));
                } else if (f.getPoints().size() == 1) {
                    return Optional
                            .of(new MoveTask(id, f.getPoints().get(0), System.currentTimeMillis() / 1000.0 + 1800));
                }
            }
        }
        return Optional.empty();
    }

    synchronized WorldModel getModel() {
        feature_id = 1;
        model = new WorldModel();
        Vector<GeoFeature> features = new Vector<>();

        for (MarkElement el : getConsole().getMission().generateMapGroup().getAllObjectsOfType(MarkElement.class)) {
            GeoFeature feature = new GeoFeature();
            Vector<MapPoint> featurePoints = new Vector<>();
            featurePoints.add(new MapPoint(el.getCenterLocation().getLatitudeRads(),
                    el.getCenterLocation().getLongitudeRads(), 0));
            feature.setPoints(featurePoints);
            feature.setFeatureId(feature_id++);
            features.add(feature);
        }

        for (PathElement el : getConsole().getMission().generateMapGroup().getAllObjectsOfType(PathElement.class)) {
            Vector<LocationType> points = el.getShapePoints();
            if (points == null || points.size() < 3)
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

        System.out.println(model.asJSON());
        feature_id = 1;
        return model;
    }

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

        addMenuItem("Tools>CoMap>Send SynchAdmin/INTERRUPT", null, a -> {
            SynchAdmin msg = new SynchAdmin();
            msg.setOp(OP.INTERRUPT);
            ImcMsgManager.getManager().sendMessageToSystem(msg, getConsole().getMainSystem());
        });

        addMenuItem("Tools>CoMap>Send World Model", null, a -> {
            ImcMsgManager.getManager().sendMessageToSystem(getModel(), getConsole().getMainSystem());
        });

        addMenuItem("Tools>CoMap>Assign Task", null, a -> {
            TaskAdim msg = new TaskAdim();
            msg.setTid(feature_id);
            msg.setOp(TaskAdim.OP.ASSIGN);
            Optional<TaskAdminArgs> arg = getTask(feature_id);
            if (arg.isPresent())
                msg.setArg(getTask(feature_id).get());
            feature_id++;
            ImcMsgManager.getManager().sendMessageToSystem(msg, getConsole().getMainSystem());
        });

        addMenuItem("Tools>CoMap>Unassign Task", null, a -> {
            feature_id--;
            TaskAdim msg = new TaskAdim();
            msg.setTid(feature_id);
            msg.setOp(TaskAdim.OP.UNASSIGN);
            ImcMsgManager.getManager().sendMessageToSystem(msg, getConsole().getMainSystem());
        });

        addMenuItem("Tools>CoMap>Request Status", null, a -> {
            TaskAdim msg = new TaskAdim();
            msg.setOp(TaskAdim.OP.STATUS_REQUEST);
            ImcMsgManager.getManager().sendMessageToSystem(msg, getConsole().getMainSystem());
        });
    }

    @Override
    public void cleanSubPanel() {
        
    }

}