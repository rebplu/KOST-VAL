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

package ch.kostceco.tools.kostval.validation.modulesiard.impl;

import java.io.File;
import java.util.Map;
import java.util.List;

import ch.kostceco.tools.kostval.exception.modulesiard.ValidationBprimaryStructureException;
import ch.kostceco.tools.kostval.validation.ValidationModuleImpl;
import ch.kostceco.tools.kostval.validation.modulesiard.ValidationBprimaryStructureModule;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;

/** Validierungsschritt B (primäre Verzeichnisstruktur) Besteht eine korrekte primäre
 * Verzeichnisstruktur? valid --> [Name].siard/header und [Name].siard/content invalid -->
 * [Name].siard/[Name]/header und [Name].siard/[Name]/content invalid --> Andere Ordner oder Dateien
 * im Toplevel-Ordner ==> Bei den Module A, B, C und D wird die Validierung abgebrochen, sollte das
 * Resulat invalid sein!
 * 
 * @author Rc Claire Roethlisberger, KOST-CECO */

public class ValidationBprimaryStructureModuleImpl extends ValidationModuleImpl implements
		ValidationBprimaryStructureModule
{

	@Override
	public boolean validate( File valDatei, File directoryOfLogfile, Map<String, String> configMap )
			throws ValidationBprimaryStructureException
	{
		boolean showOnWork = true;
		int onWork = 410;
		// Informationen zur Darstellung "onWork" holen
		String onWorkConfig = configMap.get( "ShowProgressOnWork" );
		if ( onWorkConfig.equals( "no" ) ) {
			// keine Ausgabe
			showOnWork = false;
		} else {
			// Ausgabe SIP-Modul Ersichtlich das KOST-Val arbeitet
			System.out.print( "B    " );
			System.out.print( "\b\b\b\b\b" );
		}

		Integer bExistsHeaderFolder = 0;
		Integer bExistsContentFolder = 0;

		String toplevelDir = valDatei.getName();
		int lastDotIdx = toplevelDir.lastIndexOf( "." );
		toplevelDir = toplevelDir.substring( 0, lastDotIdx );

		try {

			Zip64File zipfile = new Zip64File( valDatei );
			List<FileEntry> fileEntryList = zipfile.getListFileEntries();
			for ( FileEntry fileEntry : fileEntryList ) {

				/* nur valid wenn es mit header oder content anf�ngt dies schliesst auch
				 * [Name].siard/[Name]/header und [Name].siard/[Name]/content mit ein */
				String name = fileEntry.getName();
				if ( name.startsWith( "content/" ) ) {
					// erlaubter Inhalt content/...
					bExistsContentFolder = 1;
				} else {
					if ( name.startsWith( "header/" ) ) {
						// erlaubter Inhalt header/...
						bExistsHeaderFolder = 1;
					} else {
						// keines der beiden validen M�glichkeiten -> Fehler
						getMessageService().logError(
								getTextResourceService().getText( MESSAGE_XML_MODUL_B_SIARD )
										+ getTextResourceService().getText( MESSAGE_XML_B_NOTALLOWEDFILE, name ) );
						// SIARD enthaelt ein File, das sich nicht dort befinden duerfte: {0}
						return false;
					}
				}
				if ( showOnWork ) {
					if ( onWork == 410 ) {
						onWork = 2;
						System.out.print( "B-   " );
						System.out.print( "\b\b\b\b\b" );
					} else if ( onWork == 110 ) {
						onWork = onWork + 1;
						System.out.print( "B\\   " );
						System.out.print( "\b\b\b\b\b" );
					} else if ( onWork == 210 ) {
						onWork = onWork + 1;
						System.out.print( "B|   " );
						System.out.print( "\b\b\b\b\b" );
					} else if ( onWork == 310 ) {
						onWork = onWork + 1;
						System.out.print( "B/   " );
						System.out.print( "\b\b\b\b\b" );
					} else {
						onWork = onWork + 1;
					}
				}
			}
			zipfile.close();
			// set to null
			zipfile = null;
			if ( bExistsContentFolder == 0 ) {
				getMessageService().logError(
						getTextResourceService().getText( MESSAGE_XML_MODUL_B_SIARD )
								+ getTextResourceService().getText( MESSAGE_XML_B_CONTENT ) );
				// SIARD enthaelt kein content-Ordner
				return false;
			}
			if ( bExistsHeaderFolder == 0 ) {
				getMessageService().logError(
						getTextResourceService().getText( MESSAGE_XML_MODUL_B_SIARD )
								+ getTextResourceService().getText( MESSAGE_XML_B_HEADER ) );
				// SIARD enthaelt kein header-Ordner
				return false;
			}
		} catch ( Exception e ) {
			getMessageService().logError(
					getTextResourceService().getText( MESSAGE_XML_MODUL_B_SIARD )
							+ getTextResourceService().getText( ERROR_XML_UNKNOWN, e.getMessage() ) );
			return false;
		}
		return true;
	}
}
