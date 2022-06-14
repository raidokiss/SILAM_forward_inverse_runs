import java.io.*;
import java.util.List;

public class SILAMRun {
    private final String directionInTime; //FORWARD or INVERSE
    private final String ctrlFileName; //goes straight to case_name, without file extension
    private final String pointSourceFileName; //.v5 file, without file extension
    private final String startTime, endTime; //must be in SILAM time format
    private final String newLine = System.lineSeparator();
    private String layerThickness; //format example: "20. 20. 20. 20. 20. 20. 80. 200. 400. 1200. 2000."

    //constructor
    public SILAMRun(String directionInTime, String ctrlFileName, String pointSourceFileName, String startTime, String endTime) {
        this.directionInTime = directionInTime;
        this.ctrlFileName = ctrlFileName;
        this.pointSourceFileName = pointSourceFileName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setLayerThickness(String layerThickness) {
        this.layerThickness = layerThickness;
    }

    //methods
    public void generatePointSourceFile(List<PointSource> sources) throws Exception {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(pointSourceFileName + ".v5"))) {
            bw.write("# This is a file describing a point source for SILAM version 5, generated w/ Java" + newLine);
            for (PointSource source : sources) {
                bw.write("POINT_SOURCE_5" + newLine + newLine);

                bw.write("source_name = " + source.getSourceName() + newLine +
                        "source_sector_name = " + newLine + newLine); //does not need to be specified

                bw.write("source_longitude = " + source.getSourceLon() + newLine +
                        "source_latitude = " + source.getSourceLat() + newLine +
                        "plume_rise = YES" + newLine +
                        "release_rate_unit = g/sec" + newLine + newLine);

                bw.write("vertical_unit = m  #hpa" + newLine +
                        "stack_height = " + source.getStackHeight() + " m" + newLine + newLine);

                List<String> sourceRows = source.getSourceRows();//sourceRows see class PointSource
                for (String s : sourceRows) {
                    bw.write(s + newLine);
                }

                if (directionInTime.equals("INVERSE")) {
                    bw.write("hour_in_day_index = PASSIVE_COCKTAIL 1. 1. 1. 1. 1. 1. 1. 1. 1. 1. 1. 1. 1. 1. 1. 1. 1. 1. 1. 1. 1.  1. 1. 1." + newLine +
                            "day_in_week_index = PASSIVE_COCKTAIL 1. 1. 1. 1. 1. 1. 1." + newLine +
                            "month_in_year_index = PASSIVE_COCKTAIL 1. 1. 1. 1. 1. 1. 1. 1. 1. 1. 1. 1.");
                } else if (directionInTime.equals("FORWARD")) {
                    bw.write(newLine);
                    bw.write("hour_in_day_index = PASSIVE_COCKTAIL");
                    String[] hidi = source.getHourInDayIndex();
                    for (String s : hidi) {
                        bw.write(" " + s);
                    }
                    bw.write(newLine + "day_in_week_index = PASSIVE_COCKTAIL");
                    String[] diwi = source.getDayInWeekIndex();
                    for (String s : diwi) {
                        bw.write(" " + s);
                    }
                    bw.write(newLine + "month_in_year_index = PASSIVE_COCKTAIL");
                    String[] miyi = source.getMonthInYearIndex();
                    for (String s : miyi) {
                        bw.write(" " + s);
                    }
                }
                bw.write(newLine + newLine + "END_POINT_SOURCE_5   # MANDATORY" + newLine + newLine);
            }
        }
    }

    public void generateCtrlFile() throws IOException { //standard control file written into code: easiest solution.
        //only slight changes were needed: SILAMRun instance fields are written in
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ctrlFileName + ".control"))) {
            bw.write("# Control file for SILAM v5.4 operational run, generated w/ Java" + newLine +
                    newLine +
                    "CONTROL_V5_3" + newLine +
                    "LIST = general_parameters" + newLine +
                    "   case_name = " + ctrlFileName + newLine +
                    "   direction_in_time = " + directionInTime + newLine +
                    "   simulation_type = EULERIAN" + newLine +
                    "   start_time = " + startTime + " 0.0" + newLine +
                    "   end_time = " + endTime + " 0.0" + newLine +
                    "   time_step = 10 min" + newLine +
                    "   cut_area_source_if_outside_meteo_grid = YES" + newLine +
                    "   computation_accuracy = 20" + newLine +
                    " END_LIST = general_parameters" + newLine + newLine
            );

            bw.write(" LIST = mpi_parallel_parameters" + newLine +
                    "   x_divisions = 1" + newLine +
                    "   y_divisions = 2" + newLine +
                    "   max_wind_speed = 100 # [m/s]" + newLine +
                    "   use_mpiio = YES" + newLine +
                    "   use_mpiio_netcdf = YES" + newLine +
                    " END_LIST = mpi_parallel_parameters" + newLine + newLine);

            bw.write(" LIST = dispersion_parameters" + newLine +
                    "   grid_method = OUTPUT_GRID" + newLine +
                    "   vertical_method = OUTPUT_LEVELS    # METEO_LEVELS / OUTPUT_LEVELS / CUSTOM_LEVELS" + newLine +
                    " END_LIST = dispersion_parameters" + newLine +
                    "" + newLine +
                    " LIST = meteo_parameters" + newLine +
                    "   dynamic_meteo_file = GRIB /usr/airviro/data/Eesti/match/data//hirlam/C22_40lev/est1_%ay4%am2%ad2%ah200+%f3H00M" + newLine +
                    "   static_meteo_file = -" + newLine +
                    "    meteo_time_step = 1 hr " + newLine +
                    "   if_wait_for_data = NO" + newLine +
                    "   abl_parameterization_method = FULL_PARAM    # DRY_ABL, FULL_PARAM " + newLine +
                    "   number_of_precipitation_fields = 2" + newLine +
                    " END_LIST = meteo_parameters" + newLine + newLine);

            bw.write("LIST = emission_parameters" + newLine +
                    "   emission_source = EULERIAN " + pointSourceFileName + ".v5" + newLine +
                    "   cut_area_source_if_outside_meteo_grid = YES" + newLine +
                    "   if_technical_source_dump = NONE   # NONE / ORIGINAL_GRID / DISPERSION_GRID" + newLine +
                    " END_LIST = emission_parameters" + newLine + newLine);

            bw.write(" LIST = initial_and_boundary_conditions" + newLine +
                    " #initialize_quantity = concentration   ! if no such line, initial conditions are void" + newLine +
                    " #initialization_file = GRADS init.super_ctl" + newLine +
                    " #boundary_type =  DIRICHLET        ! ZERO / DIRICHLET   " + newLine +
                    " #if_lateral_boundary = YES         ! YES/NO " + newLine +
                    " #if_top_boundary =  YES             ! YES/NO " + newLine +
                    " #if_bottom_boundary =  NO          ! YES/NO " + newLine +
                    " #boundary_time_step =  3 hr        ! timestep unit " + newLine +
                    " #boundary_header_filename = ../common/boundary_cb4.ini" + newLine +
                    " END_LIST = initial_and_boundary_conditions" + newLine +
                    newLine +
                    " LIST = transformation_parameters" + newLine +
                    "#   transformation = PASSIVE EULERIAN " + newLine +
                    "#   transformation = PM_GENERAL EULERIAN " + newLine +
                    "#   transformation = DMAT_SULPHUR EULERIAN " + newLine +
                    "#   transformation = CB4 EULERIAN " + newLine +
                    "#   transformation = POP_GENERAL EULERIAN " + newLine +
                    "#   transformation = ACID_BASIC EULERIAN " + newLine +
                    "#   transformation = RADIOACTIVE EULERIAN " + newLine +
                    "" + newLine +
                    "#   aerosol_dynamics = SIMPLE EULERIAN " + newLine +
                    "   " + newLine +
                    "   dry_deposition_scheme = KS2011_TF" + newLine +
                    "   wet_deposition_scheme = STANDARD_3D_SCAVENGING" + newLine +
                    "    " + newLine +
                    "   if_actual_humidity_for_particle_size = YES" + newLine +
                    "   default_relative_humidity = 0.8" + newLine +
                    "   compute_thermodiffusion = NO" + newLine +
                    "   mass_low_threshold = STANDARD_ACCURACY  # CRUDE_ACCURACY, STANDARD_ACCURACY, HIGH_ACCURACY" + newLine +
                    "   " + newLine +
                    "   passive_subst_ref_lifetime = 500 day" + newLine +
                    "   passive_subst_ref_tempr = 288" + newLine +
                    "   passive_subst_dLifeTime_dT = -1 min/K" + newLine +
                    "   " + newLine +
                    "   ADB_if_compute_nucleation = YES" + newLine +
                    "   ADB_nucleation_scheme = KINETIC    # BINARY, TERNARY, KINETIC, ACTIVATION" + newLine +
                    "   ADB_if_compute_coagulation = YES" + newLine +
                    "   ADB_if_compute_condensation = YES" + newLine +
                    "   ADB_if_compute_cloud_activation = NO" + newLine +
                    "   ADB_if_recalc_wet_d = YES" + newLine +
                    "   " + newLine +
                    "   if_full_acid_chemistry = YES" + newLine +
                    "" + newLine +
                    "   make_coarse_no3 = sslt   0.03  ! material of aerosol to make it on and stickiness coef" + newLine +
                    "" + newLine +
                    " END_LIST = transformation_parameters" + newLine +
                    "" + newLine +
                    " LIST = optical_density_parameters " + newLine +
                    "   optical_coefficients_depend_on_relative_humidity = YES" + newLine +
                    "   optical_coefficients_depend_on_temperature = YES" + newLine +
                    "   if_split_aerosol_modes = YES            ! doesn't work yet" + newLine +
                    "   if_narrow_wave_bands = YES              ! doesn't work yet" + newLine +
                    " END_LIST = optical_density_parameters" + newLine +
                    "" + newLine +
                    " LIST = output_parameters" + newLine +
                    "   source_id = NO_SOURCE_SPLIT  # SOURCE_NAME  SOURCE_SECTOR  SOURCE_NAME_AND_SECTOR " + newLine +
                    "   vertical_method = CUSTOM_LAYERS" + newLine +
                    "   level_type = HEIGHT_FROM_SURFACE " + newLine +
                    "   layer_thickness = " + layerThickness + "   # output levels [m]/[pa]/[hybrid_nbr], reals" + newLine +
                    "   output_time_step = 10 min " + newLine +
                    "   output_times = REGULAR " + newLine +
                    "   output_format = NETCDF4" + newLine +
                    "   massmap_precision_factor = 128" + newLine +
                    "   time_split = ALL_IN_ONE" + newLine +
                    "   template =  output/%case" + newLine +
                    "   variable_list = ${HOME}/ini/output_config.ini" + newLine +
                    "   grid_method = CUSTOM_GRID" + newLine +
                    "   grid_type = lon_lat" + newLine +
                    "   grid_title = GEMS output grid" + newLine +
                    "   resol_flag = 128" + newLine +
                    "   ifReduced = 0 " + newLine +
                    "   earth_flag = 0" + newLine +
                    "   wind_component = 0 " + newLine +
                    "   reduced_nbr_str = 0 " + newLine +
                    "   " + newLine +
                    "   lon_start = 21.6" + newLine +
                    "   lat_start = 57.4" + newLine +
                    "   nx = 133" + newLine +
                    "   dx = 0.05" + newLine +
                    "   ny = 92" + newLine +
                    "   dy = 0.025" + newLine +
                    "" + newLine +
                    "   lat_s_pole = -90." + newLine +
                    "   lon_s_pole = 0." + newLine +
                    "   lat_pole_stretch = 0." + newLine +
                    "   lon_pole_stretch = 0." + newLine +
                    " END_LIST = output_parameters" + newLine +
                    "" + newLine +
                    " LIST = STANDARD_SETUP" + newLine +
                    "   max_hole_in_meteo_data = 6 hr" + newLine +
                    "#horizontal_advection_method_eulerian = EULERIAN_HORIZ_V5" + newLine +
                    "   #vertical_advection_method_eulerian = EULERIAN_VERT_V5" + newLine +
                    "   advection_method_eulerian = EULERIAN_V5" + newLine +
                    "   kz_profile_method = SILAM_ABL_EC_FT_KZ" + newLine +
                    "   advection_method_lagrangian = LAGRANGIAN_WIND_ENDPOINT_3D" + newLine +
                    "   random_walk_method = FULLY_MIXED" + newLine +
                    "   advection_method_default = EULERIAN" + newLine +
                    "   abl_height_method = COMBINATION" + newLine +
                    "   continuity_equation = anelastic_v2" + newLine +
                    "   horizontal_interpolation = LINEAR" + newLine +
                    "   vertical_interpolation = LINEAR" + newLine +
                    "   time_interpolation = LINEAR" + newLine +
                    "   standard_setup_directory = ${HOME}/ini/" + newLine +
                    "   nuclide_database_fnm = ^silam_nuclides.dat" + newLine +
                    "   chemical_database_fnm = ^silam_chemicals.dat" + newLine +
                    "   standard_cocktail_fnm = ^standard_chemistry_cocktails.ini" + newLine +
                    "   standard_cocktail_fnm = ^standard_aerosols_cocktails.ini" + newLine +
                    "   standard_cocktail_fnm = ^standard_auxillary_cocktails.ini" + newLine +
                    "   grib_code_table_fnm = ^grib_code_table_v5.silam" + newLine +
                    "   netcdf_name_table_fnm = ^netcdf_name_table.silam" + newLine +
                    "#   land_use_data_meta_file = data/physio/land_use_features_USGS_Eurasia.dat" + newLine +
                    "   optical_properties_meta_data_file = ^optical_properties.dat" + newLine +
                    "   photolysis_data_file = ^photolysis_finrose.dat" + newLine +
                    "   timezone_list_fnm = ^tzindex.dat" + newLine +
                    "   allow_zero_forecast_length = NO" + newLine +
                    "   precipitation_low_limit = 0.1 mm/hr" + newLine +
                    "   print_debug_info = DEBUG_INFO_YES" + newLine +
                    "   cloud_report_interval = 1" + newLine +
                    "   disregard_meteo_data_sources = YES" + newLine +
                    " END_LIST = STANDARD_SETUP" + newLine + newLine);

            bw.write("END_CONTROL_V5_3");
        }
        //commands for myself to run SILAM in my folder in Airviro (EERC) server
        String commands = "/usr/airviro/silam/silam_v5_7 /usr/airviro/users/raido/" + ctrlFileName + ".control" + newLine;
        if (directionInTime.equals("INVERSE")) {
            commands += "cd output" + newLine;
            commands += "ncpdq -a -time " + ctrlFileName + ".nc4 " + ctrlFileName + "_inv.nc4" + newLine; //reversing time command
            commands += "cd" + newLine;
        } else commands += newLine;
        System.out.println(commands); //to command line
    }
}