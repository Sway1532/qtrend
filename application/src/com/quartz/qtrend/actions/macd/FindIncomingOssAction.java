/*
 * Copyright (c) 2006 9095-2458 Quebec Inc. All Rights Reserved.
 *
 * Althought this code is consider of good quality and has been tested, it is
 * provided to you WITHOUT guaranty of any kind.
 */
package com.quartz.qtrend.actions.macd;

import com.quartz.qtrend.QTrendConstants;
import com.quartz.qtrend.actions.helpers.FindIncomingSignalAction;
import com.quartz.qtrend.dom.StockQuote;
import com.quartz.qutilities.util.Output;
import com.quartz.qutilities.jobrunner.DefaultJob;
import com.quartz.qutilities.logging.ILog;
import com.quartz.qutilities.logging.LogManager;
import com.quartz.qutilities.swing.events.QEventManager;
import com.quartz.qutilities.util.Output;

import javax.swing.*;
import java.util.EventObject;
import java.util.List;

/**
 * INSERT YOUR COMMENT HERE....
 *
 * @author Christian
 * @since Quartz...
 */
public class FindIncomingOssAction extends FindIncomingSignalAction
{
    ///////////////////////////////////////
    ////    STATIC ATTRIBUTES

    static private final ILog LOG = LogManager.getLogger(FindIncomingOssAction.class);

    ///////////////////////////////////////
    ////    STATIC METHODS

    ///////////////////////////////////////
    ////    INSTANCE ATTRIBUTES

    ///////////////////////////////////////
    ////    CONSTRUCTORS

    public FindIncomingOssAction()
    {
    }

    ///////////////////////////////////////
    ////    INSTANCE METHODS

    public void handleEvent(QEventManager pEventManager, EventObject pEvent, String pCommand)
    {
        final Output output = frame.getOutput();

        output.clear();

        try
        {
            final String manyDaysStr = JOptionPane.showInputDialog(
                    frame, "How many days ahead?", userProperties.getUserProperty(
                            QTrendConstants.UserPropertyNames.USERPROP_LAST_NEXT_SIGNAL_DAYS, "2"));
            if (manyDaysStr == null) return;
            final int manyDays = Integer.parseInt(manyDaysStr);
            userProperties.setUserProperty(QTrendConstants.UserPropertyNames.USERPROP_LAST_NEXT_SIGNAL_DAYS, "" + manyDays);

            final String rsiStr = JOptionPane.showInputDialog(frame, "RSI above?", userProperties.getUserPropertyAsInt(QTrendConstants.UserPropertyNames.USERPROP_OSS_MINIMUM_RSI, 70));
            if (rsiStr == null) return;
            final int minRsi = Integer.parseInt(rsiStr.trim());
            userProperties.setUserProperty(QTrendConstants.UserPropertyNames.USERPROP_OSS_MINIMUM_RSI, String.valueOf(minRsi));

            jobRunner.runJob(new DefaultJob()
            {
                public Object runJob() throws Exception
                {
                    final List<StockQuote> quotes = stockQuotesService.findIncomingOss(manyDays, minRsi);

                    output.writeln(QTrendConstants.Formats.DEFAULT_FORMAT_WITH_EMA56_EMA112.formatTitle(true));
                    output.writeln(QTrendConstants.Formats.DEFAULT_FORMAT_WITH_EMA56_EMA112.format(quotes));

                    return null;
                }

                public void onException(Exception e)
                {
                    LOG.error("COuld not find incoming OSS", e);
                }
            });
        }
        catch(NumberFormatException e)
        {
            LOG.error("Could not read number from input value.", e);
        }
    }

    ///////////////////////////////////////
    ////    INNER CLASSES
}
