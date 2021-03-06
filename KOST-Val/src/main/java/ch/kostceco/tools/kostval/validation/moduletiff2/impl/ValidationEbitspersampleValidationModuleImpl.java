/* == KOST-Val ==================================================================================
 * The KOST-Val application is used for validate TIFF, SIARD, PDF/A, JP2, JPEG-Files and Submission
 * Information Package (SIP). Copyright (C) 2012-2018 Claire Roethlisberger (KOST-CECO), Christian
 * Eugster, Olivier Debenath, Peter Schneider (Staatsarchiv Aargau), Markus Hahn (coderslagoon),
 * Daniel Ludin (BEDAG AG)
 * -----------------------------------------------------------------------------------------------
 * KOST-Val is a development of the KOST-CECO. All rights rest with the KOST-CECO. This application
 * is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version. BEDAG AG and Daniel Ludin hereby disclaims all copyright
 * interest in the program SIP-Val v0.2.0 written by Daniel Ludin (BEDAG AG). Switzerland, 1 March
 * 2011. This application is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the follow GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA or see
 * <http://www.gnu.org/licenses/>.
 * ============================================================================================== */

package ch.kostceco.tools.kostval.validation.moduletiff2.impl;

import java.io.BufferedReader;
import java.io.File;
import java.util.Map;
import java.io.FileReader;

import ch.kostceco.tools.kostval.exception.moduletiff2.ValidationEbitspersampleValidationException;
import ch.kostceco.tools.kostval.validation.ValidationModuleImpl;
import ch.kostceco.tools.kostval.validation.moduletiff2.ValidationEbitspersampleValidationModule;

/** Validierungsschritt E (BitsPerSample-Validierung) Ist die TIFF-Datei gem�ss Konfigurationsdatei
 * valid?
 * 
 * @author Rc Claire Roethlisberger, KOST-CECO */

public class ValidationEbitspersampleValidationModuleImpl extends ValidationModuleImpl implements
		ValidationEbitspersampleValidationModule
{

	public static String	NEWLINE	= System.getProperty( "line.separator" );

	@Override
	public boolean validate( File valDatei, File directoryOfLogfile, Map<String, String> configMap )
			throws ValidationEbitspersampleValidationException
	{

		boolean isValid = true;

		// Informationen zum Logverzeichnis holen
		String pathToExiftoolOutput = directoryOfLogfile.getAbsolutePath();
		File exiftoolReport = new File( pathToExiftoolOutput, valDatei.getName() + ".exiftool-log.txt" );
		pathToExiftoolOutput = exiftoolReport.getAbsolutePath();

		/* Nicht vergessen in "src/main/resources/config/applicationContext-services.xml" beim
		 * entsprechenden Modul die property anzugeben: <property name="configurationService"
		 * ref="configurationService" /> */

		String bps1 = configMap.get( "AllowedBitspersample1" );
		String bps2 = configMap.get( "AllowedBitspersample2" );
		String bps4 = configMap.get( "AllowedBitspersample4" );
		String bps8 = configMap.get( "AllowedBitspersample8" );
		String bps16 = configMap.get( "AllowedBitspersample16" );
		String bps32 = configMap.get( "AllowedBitspersample32" );

		if ( bps1.startsWith( "Configuration-Error:" ) ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_XML_MODUL_E_TIFF ) + bps1 );
			return false;
		}
		if ( bps2.startsWith( "Configuration-Error:" ) ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_XML_MODUL_E_TIFF ) + bps2 );
			return false;
		}
		if ( bps4.startsWith( "Configuration-Error:" ) ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_XML_MODUL_E_TIFF ) + bps4 );
			return false;
		}
		if ( bps8.startsWith( "Configuration-Error:" ) ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_XML_MODUL_E_TIFF ) + bps8 );
			return false;
		}
		if ( bps16.startsWith( "Configuration-Error:" ) ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_XML_MODUL_E_TIFF ) + bps16 );
			return false;
		}
		if ( bps32.startsWith( "Configuration-Error:" ) ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_XML_MODUL_E_TIFF ) + bps32 );
			return false;
		}

		Integer exiftoolio = 0;
		String oldErrorLine1 = "";
		String oldErrorLine2 = "";
		String oldErrorLine3 = "";
		String oldErrorLine4 = "";
		String oldErrorLine5 = "";

		try {
			BufferedReader in = new BufferedReader( new FileReader( exiftoolReport ) );
			String line;
			while ( (line = in.readLine()) != null ) {
				/* zu analysierende TIFF-IFD-Zeile die BitsPerSample-Zeile enth�lt einer dieser Freitexte
				 * der BitsPerSampleart max ist 1, 2, 4, 8, 16, 32 erlaubt
				 * 
				 * Varianten: BitsPerSample: 8 BitsPerSample: 8 8 8 BitsPerSample: 8, 8, 8 evtl noch mehr */
				if ( line.contains( "BitsPerSample: " ) ) {
					exiftoolio = 1;
					if ( ((line.contains( "BitsPerSample: 1 " ) || (line.contains( "BitsPerSample: 1," )) || (line
							.equals( "BitsPerSample: 1" ))) && bps1.equals( "1" ))
							|| ((line.contains( "BitsPerSample: 2 " ) || (line.contains( "BitsPerSample: 2," )) || (line
									.equals( "BitsPerSample: 2" ))) && bps2.equals( "2" ))
							|| ((line.contains( "BitsPerSample: 4 " ) || (line.contains( "BitsPerSample: 4," )) || (line
									.equals( "BitsPerSample: 4" ))) && bps4.equals( "4" ))
							|| ((line.contains( "BitsPerSample: 8 " ) || (line.contains( "BitsPerSample: 8," )) || (line
									.equals( "BitsPerSample: 8" ))) && bps8.equals( "8" ))
							|| ((line.contains( "BitsPerSample: 16 " ) || (line.contains( "BitsPerSample: 16," )) || (line
									.equals( "BitsPerSample: 16" ))) && bps16.equals( "16" ))
							|| ((line.contains( "BitsPerSample: 32 " ) || (line.contains( "BitsPerSample: 32," )) || (line
									.equals( "BitsPerSample: 32" ))) && bps32.equals( "32" )) ) {
						// Valid
					} else {
						// Invalider Status
						isValid = false;
						if ( !line.equals( oldErrorLine1 ) && !line.equals( oldErrorLine2 )
								&& !line.equals( oldErrorLine3 ) && !line.equals( oldErrorLine4 )
								&& !line.equals( oldErrorLine5 ) ) {
							// neuer Fehler
							getMessageService().logError(
									getTextResourceService().getText( MESSAGE_XML_MODUL_E_TIFF )
											+ getTextResourceService().getText( MESSAGE_XML_CG_INVALID, line ) );
							if ( oldErrorLine1.equals( "" ) ) {
								oldErrorLine1 = line;
							} else if ( oldErrorLine2.equals( "" ) ) {
								oldErrorLine2 = line;
							} else if ( oldErrorLine3.equals( "" ) ) {
								oldErrorLine3 = line;
							} else if ( oldErrorLine4.equals( "" ) ) {
								oldErrorLine4 = line;
							} else if ( oldErrorLine5.equals( "" ) ) {
								oldErrorLine5 = line;
							}
						}
					}
				}
			}
			if ( exiftoolio == 0 ) {
				// Invalider Status
				isValid = false;
				getMessageService().logError(
						getTextResourceService().getText( MESSAGE_XML_MODUL_E_TIFF )
								+ getTextResourceService().getText( MESSAGE_XML_CG_ETNIO, "E" ) );
			}
			in.close();
		} catch ( Exception e ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_XML_MODUL_E_TIFF )
							+ getTextResourceService().getText( MESSAGE_XML_CG_CANNOTFINDETREPORT ) );
			return false;
		}
		return isValid;
	}
}