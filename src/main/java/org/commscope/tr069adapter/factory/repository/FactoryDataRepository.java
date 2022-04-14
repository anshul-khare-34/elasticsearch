package org.commscope.tr069adapter.factory.repository;
/**
*
* Copyright (c) 2019 Commscope India. All rights reserved.
* 
*/

import org.commscope.tr069adapter.factory.model.FactoryData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 *  
 * @version 1.0
 * @since September 27, 2019
 * @author Prashant Kumar
 */
@Repository
public interface FactoryDataRepository extends CrudRepository<FactoryData, String>{

}