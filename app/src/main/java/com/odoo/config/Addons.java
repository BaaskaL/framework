/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 30/12/14 3:11 PM
 */
package com.odoo.config;

import com.odoo.addons.customers.Customers;
import com.odoo.addons.scrapAccumulator.ScrapAccumulator;
import com.odoo.core.support.addons.AddonsHelper;
import com.odoo.core.support.addons.OAddon;

import mn.odoo.addons.TechnicInspection.TechnicsInspection;
import mn.odoo.addons.employees.Employees;
import mn.odoo.addons.scrapAccumulator.ScrapAccumulators;
import mn.odoo.addons.scrapOil.ScrapOil;
import mn.odoo.addons.scrapParts.ScrapPart;
import mn.odoo.addons.scrapTechnic.ScrapTechnics;
import mn.odoo.addons.scrapTire.ScrapTire;
import mn.odoo.addons.technic.Technics;
import mn.odoo.addons.workOrder.WorkOrders;

public class Addons extends AddonsHelper {

    /**
     * Declare your required module here
     * NOTE: For maintain sequence use object name in asc order.
     * Ex.:
     * OAddon partners = new OAddon(Partners.class).setDefault();
     * for maintain sequence call withSequence(int sequence)
     * OAddon partners = new OAddon(Partners.class).withSequence(2);
     */
    OAddon customers = new OAddon(Customers.class).setDefault();
    OAddon technic = new OAddon(Technics.class);
    OAddon technicInspection = new OAddon(TechnicsInspection.class);
    OAddon scrapParts = new OAddon(ScrapPart.class);
    OAddon scrapTire = new OAddon(ScrapTire.class);
    OAddon scrapOil = new OAddon(ScrapOil.class);
    OAddon employees = new OAddon(Employees.class);
    OAddon scrapTechnic = new OAddon(ScrapTechnics.class);
    OAddon work_order = new OAddon(WorkOrders.class);
    OAddon scrapAccumulator= new OAddon(ScrapAccumulators.class);
}
