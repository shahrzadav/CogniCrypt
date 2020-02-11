/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.dom4j.io.XMLWriter;

import de.cognicrypt.codegenerator.question.Answer;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.utils.FileHelper;
import de.cognicrypt.utils.Utils;

import org.json.simple.JSONObject;
/**
 * This class is a storage for the configuration chosen by the user.
 *
 * @author Stefan Krueger
 */
public abstract class Configuration {

	final protected Map<Question, Answer> options;
	final protected String pathOnDisk;
	protected Answer answr;
	final protected String taskName;

	@SuppressWarnings("unchecked")
	public Configuration(Map<?, ?> constraints, String pathOnDisk, String taskName) throws IOException {

		this.answr = new Answer();
		this.pathOnDisk = pathOnDisk;
		this.options = (Map<Question, Answer>) constraints;
		this.taskName = taskName;
		
		JSONObject obj = new JSONObject();
		this.options.forEach((question,answer) ->obj.put(question.getQuestionText(), answer.getValue()));
		String jsonPath = Utils.getCurrentProject().getLocation().toOSString() + "/" +Constants.pathToInstanceFile + taskName + ".json";

		File file=new File(jsonPath);  
        file.createNewFile();  
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(obj.toJSONString());  
        fileWriter.flush();  
        fileWriter.close();
	}

	/**
	 * Writes chosen configuration to hard disk.
	 *
	 * @return Written file.
	 * @throws IOException
	 *         see {@link FileWriter#FileWriter(String)) FileWriter} and {@link XMLWriter#write(String) XMLWriter.write()}
	 */
	public abstract File persistConf() throws IOException;

	public abstract List<String> getProviders();

	public String getPath() {
		return pathOnDisk;
	}

	/**
	 * Deletes config file from hard disk.
	 */
	public void deleteConfFromDisk() {
		FileHelper.deleteFile(this.pathOnDisk);
	}
}
