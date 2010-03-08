/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.cirg.cida.experiment;

import java.util.List;
import net.sourceforge.cilib.io.StandardDataTable;
import net.sourceforge.cilib.type.types.Numeric;
import net.sourceforge.cilib.type.types.Real;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author andrich
 */
public interface IExperiment {

    StandardDataTable<Numeric> getData();

    String getDataSource();

    int getId();

    String getName();

    List<String> getVariableNames();

    List<Numeric> getBottomRow();

    DescriptiveStatistics getBottomRowStatistics(String variableName);

    List<Numeric> getIterationColumn();

    List<DescriptiveStatistics> getStatistics(String variableName);

    List<Real> getStatistic(String variableName, VariableStatistic statistic);

    void initialise();

    void setData(StandardDataTable<Numeric> data);

    void setDataSource(String dataSource);

    void setId(int id);

    void setName(String name);

    void setVariableNames(List<String> variableNames);
}
