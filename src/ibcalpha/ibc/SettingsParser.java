// This file is part of IBC.
// Copyright (C) 2004 Steven M. Kearns (skearns23@yahoo.com )
// Copyright (C) 2004 - 2018 Richard L King (rlking@aultan.com)
// For conditions of distribution and use, see copyright notice in COPYING.txt

// IBC is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// IBC is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with IBC.  If not, see <http://www.gnu.org/licenses/>.

package ibcalpha.ibc;

import java.io.File;
import java.util.Date;
import java.util.List;


public class SettingsParser {
    private Settings _settings;

    public SettingsParser(Settings settings) {
        _settings = settings;
    }

    public void parse(String path) {
        Utils.logToConsole("Settings file is: " + path);

        PropertyParser parser = new PropertyParser();
        parser.load(path);

        _parseCommandServer(parser);
        _parseTwsSettings(parser);
        _parseTwsSaveSettingsAt(parser);
        _parseTradingMode(parser);
        _parseApiCredentials(parser);
        _parseFixCredentials(parser);
    }

    private void _parseTwsSettings(PropertyParser parser) {
        String path = parser.getString("IbDir", "");
        if (! path.isEmpty()) {
            _settings.setIbDir(path);
        }

        int port = parser.getInt("OverrideTwsApiPort", 0);
        if (port != 0) {
            _settings.setOverrideTwsApiPort(port);
        }

        if (! parser.getString("StoreSettingsOnServer", "").isEmpty()) {
            boolean store = parser.getBoolean("StoreSettingsOnServer", false);
            _settings.setStoreSettingsOnServer(store);
        }

        if (! parser.getString("ReadOnlyApi", "").isEmpty()) {
            boolean readonly = parser.getBoolean("ReadOnlyApi", true);
            _settings.setReadonlyApi(readonly);
        }

        String action = parser.getString("AcceptIncomingConnectionAction", "manual");
        _settings.setIncomingConnectionPolicy(IncomingConnectionPolicy.fromString(action));

        action = parser.getString("ExistingSessionDetectedAction", "manual");
        _settings.setExistingSessionPolicy(
            ExistingSessionPolicy.fromString(action)
        );

        action = parser.getString("LogComponents", "never");
        _settings.setComponentLogPolicy(ComponentLogPolicy.fromString(action));

        String time = parser.getString("ClosedownAt", "");
        if (! time.isEmpty()) {
            _settings.setShutdownTime(TimeParser.parse(time));
        }

        _settings.setFixEnabled(parser.getBoolean("FIX", false));
        _settings.setShowAllTrades(parser.getBoolean("ShowAllTrades", false));
        _settings.setLogToConsole(parser.getBoolean("LogToConsole", false));
        _settings.setAcceptNonBrokerageAccountWarning(
            parser.getBoolean("AcceptNonBrokerageAccountWarning", true));
        _settings.setReadonlyLogin(parser.getBoolean("ReadOnlyLogin", false));
        _settings.setMinimizeMainWindow(
            parser.getBoolean("MinimizeMainWindow", false));
        _settings.setAutoClosedown(
            parser.getBoolean("IbAutoClosedown", false));
        _settings.setAllowBlindTrading(
            parser.getBoolean("AllowBlindTrading", false));
        _settings.setDismissPasswordExpiry(
            parser.getBoolean("DismissPasswordExpiryWarning", false));
        _settings.setDismissNseCompliance(
            parser.getBoolean("DismissNSEComplianceNotice", true));
        _settings.setSendTwsLogsToConsole(
            parser.getBoolean("SendTWSLogsToConsole", false));
    }

    private void _parseTwsSaveSettingsAt(PropertyParser parser) {
        // setting format: SaveTwsSettingsAt=hh:mm [hh:mm]...
        //             or: SaveTwsSettingsAt=Every n [{mins | hours}] [hh:mm [hh:mm]]
        String timesSetting = parser.getString("SaveTwsSettingsAt", "");
        if (timesSetting.isEmpty()) {
            return;
        }

        try {
            _settings.setSaveTwsSettingsAt(IntervalParser.parse(timesSetting));
        } catch (IbcException e) {
            Utils.logError("Invalid setting SaveTwsSettingsAt=" + timesSetting + ": " + e.getMessage() + "\nTWS Settings will not be saved automatically");
        }
    }

    private void _parseCommandServer(PropertyParser parser)
    {
        String commandPrompt = parser.getString("CommandPrompt", "");
        if (! commandPrompt.isEmpty()) {
            _settings.setCommandServerPrompt(commandPrompt);
        }

        String bindAddress = parser.getString("BindAddress", "");
        if (! bindAddress.isEmpty()) {
            _settings.setCommandServerBindAddress(bindAddress);
        }

        int port = parser.getInt("CommandServerPort", 0);
        if (port != 0) {
            _settings.setCommandServerPort(port);
        }

        String controlFrom = parser.getString("ControlFrom", "");
        if (! controlFrom.isEmpty()) {
            _settings.setCommandServerControlFrom(controlFrom);
        }

        boolean suppress = parser.getBoolean("SuppressInfoMessages", true);
        _settings.setSuppressInfoMessages(suppress);
    }

    private void _parseTradingMode(PropertyParser parser)
    {
        String mode = parser.getString("TradingMode", "");
        if (! mode.isEmpty()) {
            Utils.logToConsole("Trading mode set from config: " + mode);
            _settings.setTradingMode(TradingMode.fromString(mode));
        }
    }

    private void _parseApiCredentials(PropertyParser parser)
    {
        String ibLoginId = parser.getString("IbLoginId", "");
        if (! ibLoginId.isEmpty()) {
            Utils.logToConsole("IB username set from config: " + ibLoginId);
            _settings.setIbLoginId(ibLoginId);
        }

        String ibPassword = parser.getString("IbPassword", "");
        if (! ibPassword.isEmpty()) {
            Utils.logToConsole("IB password set from config");
            _settings.setIbPassword(ibPassword);
        }
    }

    private void _parseFixCredentials(PropertyParser parser)
    {
        String fixLoginId = parser.getString("FIXLoginId", "");
        if (! fixLoginId.isEmpty()) {
            Utils.logToConsole("FIXLoginId set from config: " + fixLoginId);
            _settings.setFixLoginId(fixLoginId);
        }

        String fixPassword = parser.getString("FIXPassword", "");
        if (! fixPassword.isEmpty()) {
            Utils.logToConsole("FIXPassword set from config: " + fixPassword);
            _settings.setFixPassword(fixPassword);
        }
    }
}