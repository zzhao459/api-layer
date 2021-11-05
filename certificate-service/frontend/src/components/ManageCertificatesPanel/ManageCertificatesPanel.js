import React, { useState, useMemo } from "react";
import { render } from "react-dom";
import Styles from "./Style";
import { useForm, useField } from "react-final-form-hooks";

const ManageCertificatesPanel = () => {
    const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));

    const onSubmit = async values => {
        await sleep(300);
        window.alert(JSON.stringify(values, 0, 2));

        fetch("/certificate/upload", {
            method: 'POST',
            mode: 'cors',
            body: JSON.stringify({
                url: alias.input.value,
                certificate: certificate.input.value
            })
        });
    };

    const validate = values => {
        const errors = {};
        if (!values.url) {
            errors.url = "Required";
        }
        if (!values.lastName) {
            errors.lastName = "Required";
        }
        return errors;
    };

    const initialValues = useMemo(
        () => ({
            url: "https://localhost:8080",
            certificate: ""
        }),
        []
    );

    const { form, handleSubmit, values, pristine, submitting } = useForm({
        onSubmit,
        initialValues,
        validate
    });

    const alias = useField("alias", form);
    const certificate = useField("certificate", form);

    const removeCertificate = (name) => {
        console.log(`remove certificate ${name}`);
    }

    const addCertificate = () => {
        console.log("Add Certificate");
    }

    return (
        <Styles>
            <h2>Manage certificates</h2>
            <table>
                <tr>
                    <td>Label</td>
                    <td>Action</td>
                </tr>

                <tr>
                    <td>Label 1</td>
                    <td><button onClick={() => removeCertificate("Label 1")}>Remove</button></td>
                </tr>

                <tr>
                    <td>Label 2</td>
                    <td><button onClick={() => removeCertificate("Label 2")}>Remove</button></td>
                </tr>
            </table>
            <form>
                <div>
                    <label >Alias</label>
                    <input {...alias.input} placeholder="Certificate Alias" />
                    {alias.meta.touched && alias.meta.error && (
                        <span>{alias.meta.error}</span>
                    )}
                </div>

                <div>
                    <label>Certificate to use</label>
                    <input {...certificate.input} type="textarea" placeholder="Provide base64 encoded certificate." />
                    {certificate.meta.touched && certificate.meta.error && (
                        <span>{certificate.meta.error}</span>
                    )}
                </div>

                <div className="buttons">
                    <button type="submit" onClick={() => addCertificate()}>
                        Add
                    </button>
                </div>
            </form>
        </Styles>
    );

}


export default ManageCertificatesPanel;


