import React, { useState, useMemo } from "react";
import { render } from "react-dom";
import Styles from "./Style";
import { useForm, useField } from "react-final-form-hooks";
import LoadingButton from '@mui/lab/LoadingButton';
import {Box, FormControlLabel, Switch} from "@material-ui/core";
import {TextareaAutosize} from "@mui/material";

const ManageCertificatesPanel = () => {
    const sleep = ms => new Promise(resolve => setTimeout(resolve, ms));
    const [pemCert, setPemCert] = React.useState(null);
    const [serviceUrl, setServiceUrl] = React.useState(null);
    const [certAlias, setCertAlias] = useState(null);
    const [errors, setErrors] = React.useState(null);
    const [loading, setLoading] = React.useState(false);
    function handleClick() {
        setLoading(true);
    }
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
        setCertAlias(values.alias);
        setServiceUrl(values.url);

        if (!values.url) {
            errors.url = "Required";
        }
        if (!values.alias) {
            errors.alias = "Required";
        }
        return errors;
    };

    const initialValues = useMemo(
        () => ({
            alias: "",
            url: "",
            certificate: ""
        }),
        []
    );

    // TODO get list of certificate

    const getCertificateInPemFormat = async () => {
        const url = process.env.REACT_APP_GATEWAY_URL + `/api/v1/certificate-service/certificate?url=${serviceUrl}`
        fetch(url, {
            method: 'GET',
            mode: 'cors'
        }).then((response) => {
            if (!response.ok) {
                throw Error(response.statusText);
            }
            return response.text();
        }).then(result => {
            setPemCert(result);
        }).catch((error) => {
            console.log(error)
            setErrors(error.message);
        });
    }

    const { form, handleSubmit, values, pristine, submitting } = useForm({
        onSubmit,
        initialValues,
        validate
    });

    const alias = useField("alias", form);
    const url = useField("url", form);
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
                <Box sx={{ '& > button': { m: 1 } }}>
                    <FormControlLabel
                        sx={{
                            display: 'block',
                        }}
                        control={
                            <Switch
                                checked={loading}
                                onChange={() => setLoading(!loading)}
                                name="loading"
                                color="primary"
                            />
                        }
                        label="Retrieve certificate"
                    />
                </Box>
                <div>
                    <label disabled={!loading} >Hostname</label>
                    <input disabled={!loading} {...url.input} placeholder="Service Hostname" />
                    {url.meta.touched && url.meta.error && (
                        <span>{url.meta.error}</span>
                    )}
                </div>
                <div className="buttons">
                    <button disabled={!loading} type="submit" onClick={getCertificateInPemFormat}>
                        Get certificate
                    </button>
                </div>
                { pemCert !== null && (
                    <TextareaAutosize
                        aria-label="minimum height"
                        minRows={3}
                        defaultValue={pemCert}
                        style={{ width: 200 }}
                    />
                )}
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


