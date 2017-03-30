package com.daffodil.documentumie.dctm;

import com.daffodil.documentumie.dctm.api.CSServices; 
import com.daffodil.documentumie.dctm.apiimpl.CSServicesProvider; 

public class CSServiceProviderFactory {

    public static CSServices getCSServiceProvider (String dVersion){
        return new CSServicesProvider();
    }

}

